import scala.io.Source
import scala.util.matching.Regex
import sys.process._
import scala.util.control.Breaks._
 
object me2sex {
 
    def main(args: Array[String]): Unit = {
 
        val url = List(
            "http://me2-sex.lofter.com/tag/美女摄影?page=",
            "http://me2-sex.lofter.com/tag/欧美?page=",
            "http://me2-sex.lofter.com/tag/模特?page=",
            "http://me2-sex.lofter.com/tag/美媛馆?page="
        )
 
        //遍历分页列表，获取图集URL
        url.map{ u =>
            (1 to 10).map{page_id =>
                var page_url = u+page_id
 
                val list = parseUrl(page_url, """<a class="img" href="(.*?)">""".r)
 
                printf("%s，发现 %d枚 妹子\n", page_url, list.length)
 
                //进入图集详情页，下载图集中的所有图片
                list.map{ablum_url =>
                    val photo = parseUrl(ablum_url, """<a href="#" class="img imgclasstag" imggroup="gal".*?bigimgsrc="(.*?)">""".r)
 
                    //调用系统命令在后台下载
                    photo.map{
                        printf("%s", ".")
                        img_url=> "wget -q -P me2sex " + img_url !!
                    }
                }
 
                //当列表数不够30篇时，不再查找下一页列表的内容
                if(list.length < 30)
                    break
            }
        }
    }
 
    //简易的正则获取函数
    def parseUrl(page_url : String, Pattern : Regex) : List[String] = {
        try {
            val s = Source.fromURL(page_url, "UTF-8")
            val h = s.mkString
            s.close()
 
            val urls = Pattern.findAllIn(h)
            var list = List[String]()
            for (Pattern(href) <- urls)
                list = href :: list
 
            return list
        } catch {
            case e: Exception => println("出错了\n"+e);System.exit(1)
        }
 
        return List[String]()
    }
}
