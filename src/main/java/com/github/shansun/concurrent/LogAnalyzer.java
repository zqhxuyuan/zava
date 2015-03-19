package com.github.shansun.concurrent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-11-12
 */
public class LogAnalyzer {

    private static final String				LOG_PATTNER	= "(\\d{4}-\\d{2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})\\Q \\E([A-Z]{4,5})\\Q \\E(.*?)\\Q - \\E(.*)";

    // private static final Pattern pattern = Pattern.compile(LOG_PATTNER);

    private static final Queue<LogEntry>	entries		= new ConcurrentLinkedQueue<LogEntry>();

    private static final Queue<String>		files		= new ConcurrentLinkedQueue<String>();

    private static AtomicInteger			threadCnt	= new AtomicInteger(0);

    private static String					seperator	= System.getProperty("file.separator");

    static abstract class LogEntry {
        String	bizId = "";
        String	time;
        String	using;
        String	succ;
    }

    static class LogReduceEntry extends LogEntry {
        String	subId;
        String	user;
    }

    static class LogWithholdEntry extends LogEntry {
        String	enough;
    }

    static class LogReduceInfoEntry extends LogEntry {
        String subId;
        String itemId;
        String skuId;
        String reason = "";
        int    lineCnt = 0;
    }

    static class LogFullWithholdEntry extends LogWithholdEntry {
        String code = "";
        String afterPayment = "";
        String subId = "";
        String itemId = "";
        String quantity = "";
    }

    static class LogFlusher implements Runnable {
        BufferedWriter	reduceWriter	= null;
        BufferedWriter	withholdWriter	= null;
        BufferedWriter	reduceInfoWriter= null;
        BufferedWriter	fullWithholdWriter= null;

        public LogFlusher(String base) throws UnsupportedEncodingException, FileNotFoundException {
            // reduceWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(base + "reduce.csv"), "gb2312"));
            // withholdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(base + "withhold.csv"), "gb2312"));
            // reduceInfoWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(base + "reduce-failure-2.csv"), "gb2312"));
            fullWithholdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(base + "full-withhold.csv"), "gb2312"));
        }

        public void start() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            LogEntry entry = null;

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            while (true) {
                entry = entries.poll();

                if (entry == null) {
                    if (threadCnt.get() == 0) {
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    try {
                        if (entry instanceof LogReduceEntry) {
                            LogReduceEntry re = (LogReduceEntry) entry;
                            reduceWriter.append(re.bizId + "," + re.subId + "," + re.time + "," + re.using + "," + re.succ + "\n");
                            reduceWriter.flush();
                        } else if(entry instanceof LogFullWithholdEntry) {
                            LogFullWithholdEntry fw = (LogFullWithholdEntry) entry;
                            fullWithholdWriter.append(fw.time + "," + fw.bizId + "," + fw.subId + "," + fw.itemId + "," + fw.afterPayment + "," + fw.succ + "," + fw.code + "\n");
                            fullWithholdWriter.flush();
                        } else if(entry instanceof LogWithholdEntry) {
                            LogWithholdEntry we = (LogWithholdEntry) entry;
                            withholdWriter.append(we.bizId + "," + we.time + "," + we.using + "," + we.succ + "," + we.enough + "\n");
                            withholdWriter.flush();
                        } else if(entry instanceof LogReduceInfoEntry) {
                            LogReduceInfoEntry ri = (LogReduceInfoEntry) entry;
                            reduceInfoWriter.append(ri.time + "," + ri.bizId + "," + ri.subId + "," + ri.itemId + "," + ri.skuId + "," + ri.reason + "\n");
                            reduceInfoWriter.flush();
                        }
                    } catch (IOException e) {
                    }
                }
            }

            System.out.println("Analyzer Done!");
        }
    }

    static class ReduceAnalyzer {
        BufferedReader	reader			= null;

        String IC_HSF = "HSFTimeOutException";
        String DB_EXCEPTION_1 = "NoMoreDataSourceException";
        String DB_EXCEPTION_2 = "UncategorizedSQLException"; // Could not create connection; - nested throwable: (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Could not create connection to database server. Attempted reconnect 3 times. Giving up.); - nested throwable: (com.taobao.datasource.resource.JBossResourceException: Could not create connection; - nested throwable: (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Could not create connection to database server. Attempted reconnect 3 times. Giving up.)); nested exception is com.ibatis.common.jdbc.exception.NestedSQLException:
        String DB_EXCEPTION_3 = "DataAccessResourceFailureException";
        String ITEM_NOT_ENOUGH = "IC_ITEM_QUANTITY_NOT_ENOUGH_FOR_BUY";
        String QUERY_DETAIL_FAILURE = "查询库存明细纪录失败";
        String ORDER_FINISH = "更新失败：完成状态更新库存明细失败";
        String UPDATE_DETAIL_FAILURE = "更新库存明细失败";
        String SKU_NOT_ENOUGH = "IC_SKU_QUANTITY_NOT_ENOUGH_FOR_BUY";
        String INSERT_DTL_FAILURE = "插入库存明细失败";
        String IC_ITEM_QUANTITY_OPTIMISTIC_LOCKING = "IC_ITEM_QUANTITY_OPTIMISTIC_LOCKING_FOR_BUY";

        String filepath	= null;

        public ReduceAnalyzer(String filepath) throws UnsupportedEncodingException, FileNotFoundException {
            this.filepath = filepath;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "gb2312"));
        }

        public void execute() throws IOException {
            // 日志行
            LogReduceInfoEntry entry = null;
            boolean matched = false;

            Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - reduceQuantityByBizOrderId减库存失败IpmException) 主订单=(\\d*),子订单=(\\d*),icItemId=(\\d*),skuId=(\\d*)(.*)");

            // 日志内容行
            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    if (matched) {
                        // 输出到文件
                        entries.add(entry);
                    }

                    entry = new LogReduceInfoEntry();
                    entry.time = matcher.group(2);
                    entry.bizId = matcher.group(4);
                    entry.subId = matcher.group(5);
                    entry.itemId = matcher.group(6);
                    entry.skuId = matcher.group(7);

                    matched = match(entry, matched, line);
                } else if (!matched) {
                    matched = match(entry, matched, line);
                }

                line = reader.readLine();
            }

            System.out.println(filepath + ": Done!");

            reader.close();
        }

        public boolean match(LogReduceInfoEntry entry, boolean matched, String line) {
            if(entry == null) {
                return matched;
            }

            if(line.contains(IC_HSF)) {
                entry.reason = "IC_ERROR";
                matched = true;
            } else if(line.contains(DB_EXCEPTION_1)) {
                entry.reason = DB_EXCEPTION_1;
                matched = true;
            } else if(line.contains(DB_EXCEPTION_2)) {
                entry.reason = DB_EXCEPTION_2;
                matched = true;
            } else if(line.contains(DB_EXCEPTION_3)) {
                entry.reason = DB_EXCEPTION_3;
                matched = true;
            } else if(line.contains(ITEM_NOT_ENOUGH)) {
                entry.reason = "ITEM_NOT_ENOUGH";
                matched = true;
            } else if(line.contains(QUERY_DETAIL_FAILURE)) {
                entry.reason = "QUERY_DTL_ERROR";
                matched = true;
            } else if(line.contains(ORDER_FINISH)) {
                entry.reason = "ORDER_FINISH";
                matched = true;
            } else if(line.contains(UPDATE_DETAIL_FAILURE)) {
                entry.reason = "UPDATE_DTL_FAILURE";
                matched = true;
            } else if(line.contains(SKU_NOT_ENOUGH)) {
                entry.reason = "SKU_NOT_ENOUGH";
                matched = true;
            } else if(line.contains(INSERT_DTL_FAILURE)) {
                entry.reason = "INSERT_DTL_FAILURE";
                matched = true;
            } else if(line.contains(IC_ITEM_QUANTITY_OPTIMISTIC_LOCKING)) {
                entry.reason = "IC_ITEM_QUANTITY_OPTIMISTIC_LOCKING";
                matched = true;
            }  else {
                if(entry.lineCnt < 2) {
                    entry.reason += line.replace(",", "[sp]").replace(" ", "");
                    entry.lineCnt++;
                    matched = false;
                } else {
                    matched = true;
                }
            }
            return matched;
        }
    }


    static class LogFormatter {
        BufferedReader	reader			= null;

        String			reduceKey		= "reduce";
        String			withholdKey		= "withhold";

        Pattern			reduceRegex		= Pattern
                .compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - Final-reduce~IA .*?,tc,)(\\d{1,5})ms\\] orderid=(\\d*) user_id=(\\d*) order_sub_ids=(\\d*)/ \\[(T|F->.*)\\]");
        Pattern			withholdRegex	= Pattern
                .compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - P1-WithholdInventory-Batch .*?,tc-alipay,)(\\d{1,5})ms\\] orderid=(\\d*) \\[(T|F->.*)\\] ,(Enough|Not-Enough)");

        Pattern 		fullWithholdRegex = Pattern.compile("");

        public LogFormatter(String filepath) throws UnsupportedEncodingException, FileNotFoundException {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "gb2312"));
        }

        public void execute() {
            threadCnt.incrementAndGet();

            // 日志内容行
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e1) {
            }

            while (line != null) {
                Matcher matcher = reduceRegex.matcher(line);

                if (matcher.find()) {
                    LogReduceEntry entry = new LogReduceEntry();
                    entry.time = matcher.group(2);
                    entry.bizId = matcher.group(5);
                    entry.subId = matcher.group(7);
                    entry.succ = matcher.group(8);
                    entry.user = matcher.group(6);
                    entry.using = matcher.group(4);
                    entries.add(entry);
                } else {
                    matcher = withholdRegex.matcher(line);
                    if (matcher.find()) {
                        LogWithholdEntry entry = new LogWithholdEntry();
                        entry.time = matcher.group(2);
                        entry.using = matcher.group(4);
                        entry.bizId = matcher.group(5);
                        entry.succ = matcher.group(6);
                        entry.enough = matcher.group(7);
                        entries.add(entry);
                    }
                }

                try {
                    line = reader.readLine();
                } catch (IOException e) {
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
            }

            threadCnt.decrementAndGet();
        }

    }

    static class LogFullWithholdFormatter {
        BufferedReader	reader			= null;

        String			reduceKey		= "reduce";
        String			withholdKey		= "withhold";

        Pattern			batchWithholdRegex		= Pattern
                .compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - .*WithholdInventory-Batch.*?,.*,)(\\d{1,5})ms\\] orderid=(\\d*) \\[(T|F->.*)\\] ,Not-Enough");
        Pattern			singleWithholdRegex	= Pattern
                .compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - .*WithholdInventory-Single.*?,.*,)(\\d{1,5})ms\\] suborderid=(\\d*),quantity=(\\d*),AfterPayment=(true|false),item=(\\d*) \\[(T|F->.*)\\] (.*),Not-Enough");

        Pattern 		fullWithholdRegex = Pattern.compile("");

        public LogFullWithholdFormatter(String filepath) throws UnsupportedEncodingException, FileNotFoundException {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "gb2312"));
        }

        public void execute() {
            threadCnt.incrementAndGet();

            // 日志内容行
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e1) {
            }

            while (line != null) {
                Matcher matcher = batchWithholdRegex.matcher(line);

                try {
                    if (matcher.find()) {
                        LogFullWithholdEntry entry = new LogFullWithholdEntry();
                        entry.time = matcher.group(2);
                        entry.bizId = matcher.group(5);
                        entry.succ = matcher.group(6).equals("T") + "";
                        // entry.enough = matcher.group(7).equals("Enough") + "";
                        entry.using = matcher.group(4);
                        if(!entry.succ.equals("true")) {
                            entry.code = matcher.group(6).substring(3);
                        }
                        entries.add(entry);
                    } else {
                        matcher = singleWithholdRegex.matcher(line);
                        if (matcher.find()) {
                            LogFullWithholdEntry entry = new LogFullWithholdEntry();
                            entry.time = matcher.group(2);
                            entry.using = matcher.group(4);
                            entry.subId = matcher.group(5);
                            entry.quantity = matcher.group(6);
                            entry.afterPayment = matcher.group(7);
                            entry.itemId = matcher.group(8);
                            String succ = matcher.group(9);
                            entry.succ = succ.equals("T") + "";
                            if(!entry.succ.equals("true")) {
                                entry.code = succ.substring(3, succ.indexOf("F->", 4));
                            }
                            entries.add(entry);
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                try {
                    line = reader.readLine();
                } catch (IOException e) {
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
            }

            threadCnt.decrementAndGet();
        }
    }

    static class LogFormatTask implements Runnable {

        private String	baseDir;

        public LogFormatTask(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void run() {
            while (true) {
                String filename = files.poll();
                if (filename == null) {
                    break;
                }

                try {
                    new LogFormatter(baseDir + seperator + filename).execute();
                } catch (Exception e) {
                }
            }
        }
    }

    static class LogFullWithholdTask implements Runnable {
        private String	baseDir;

        public LogFullWithholdTask(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void run() {
            while (true) {
                String filename = files.poll();
                if (filename == null) {
                    break;
                }

                try {
                    new LogFullWithholdFormatter(baseDir + seperator + filename).execute();
                } catch (Exception e) {
                }
            }
        }
    }

    static class LogReduceTask implements Runnable {
        private String	baseDir;

        public LogReduceTask(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void run() {
            threadCnt.incrementAndGet();

            while (true) {
                String filename = files.poll();
                if (filename == null) {
                    break;
                }

                try {
                    new ReduceAnalyzer(baseDir + seperator + filename).execute();
                } catch (Exception e) {
                }
            }

            threadCnt.decrementAndGet();
        }

    }

    public static void main(String[] args) throws IOException {
        realMain(args);

        // demo(args);
        // regex();
    }

    public static void realMain(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
        File folder = null;
        if (args.length == 0) {
            folder = new File(".");
        } else {
            folder = new File(args[0]);
        }

        String[] list = folder.list();
        for (String filename : list) {
            if (filename.contains("inventoryplatform")) {
                files.add(filename);
            }
        }

        int cnt = 1;
        if(args.length > 1) {
            cnt = Integer.valueOf(args[1]);
        }
        for (int i = 0; i < cnt; i++) {
            new Thread(new LogFullWithholdTask(folder.getAbsolutePath())).start();
        }

        File outputFolder = new File(folder.getAbsolutePath() + seperator + "csv" + seperator);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }

        new LogFlusher(folder.getAbsolutePath() + seperator + "csv" + seperator).start();
    }

    public static void regex() {
        String log = "2012-11-11 00:20:11 WARN IPM-Trade - P1-WithholdInventory-Batch [172.23.230.184,tc-alipay,3008ms] orderid=194630958498828 [T] ,Not-Enough";
        String log2 = "2012-11-11 00:20:07 WARN IPM-Trade - P1-WithholdInventory-Batch [172.24.168.109,tc-alipay,1016ms] orderid=194638954807578 [F->IP_ILLEGAL_INVENTORY_STATUS_Complete] ,Not-Enough";
        String log3 = "2012-11-11 00:07:18 WARN IPM-Trade - P1-WithholdInventory-Batch-Buy [172.23.36.66,tmall_buy,10015ms] orderid=194654110384883 [T] ,Not-Enough";
        String log4 = "2012-11-11 00:07:13 WARN IPM-Trade - P1-WithholdInventory-Single[172.23.180.55,tf,1ms] suborderid=246481681998782,quantity=1,AfterPayment=true,item=16269325560 [F->IP_QUERY_Inventory_Detail_ERRORF->IpmException查询预扣详情错误 子订单号：子订单号=246481681998782,itemId=16269325560] I->IpmException查询预扣详情错误 子订单号：子订单号=246481681998782,itemId=16269325560,Not-Enough,null";
        String regex = "(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - .*WithholdInventory-Batch.*?,.*,)(\\d{1,5})ms\\] orderid=(\\d*) \\[(T|F->.*)\\] ,(Enough|Not-Enough)";
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile("(\\d{4}-\\d{2}-\\d{1,2}) (\\d{1,2}:\\d{1,2}:\\d{1,2}) (.*? - .*WithholdInventory-Single.*?,.*,)(\\d{1,5})ms\\] suborderid=(\\d*),quantity=(\\d*),AfterPayment=(true|false),item=(\\d*) \\[(T|F->.*)\\] (.*),(Enough|Not-Enough)");
        Matcher matcher = pattern.matcher(log2);
        if (matcher.find()) {
            int groupCount = matcher.groupCount();
            int i = 1;
            while (i <= groupCount) {
                System.out.println(matcher.group(i++));
            }
        }
    }

    public static void demo(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        if (args.length < 3) {
            System.out.println("Usage: java LogAnalyzer filepath output matchedKey1 matchedKey2");
            return;
        }

        // 日志行
        String logLine = null;
        boolean matched = false;

        Pattern pattern = Pattern.compile(LOG_PATTNER);

        // 输入文件路径
        String filePath = args[0];

        // 输出文件路径
        String output = null;
        if ("default".equalsIgnoreCase(args[1])) {
            output = filePath + ".output";
        } else {
            output = args[1];
        }

        // 匹配关键字
        String[] matchedKeys = new String[args.length - 2];

        for (int i = 2, j = 0; i < args.length; i++, j++) {
            matchedKeys[j] = args[i];
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "gb2312"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "gb2312"));

        // 日志内容行
        String line = reader.readLine();
        while (line != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                if (matched) {
                    // 输出到文件
                    writer.append(logLine);
                }

                logLine = line;

                for (String key : matchedKeys) {
                    matched = line.contains(key);
                    if (!matched)
                        break;
                }
            } else {
                if (!matched) {
                    for (String key : matchedKeys) {
                        matched = line.contains(key);
                        if (!matched)
                            break;
                    }
                }

                logLine += line + "\n";
            }

            line = reader.readLine();
        }

        reader.close();

        writer.close();
    }
}