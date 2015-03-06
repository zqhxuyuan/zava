var node = {
    async: require('async'),
    cheerio: require('cheerio'),
    ejs: require('ejs'),
    fs: require('fs'),
    mkdirp: require('mkdirp'),
    path: require('path'),
    request: require('request'),
    url: require('url'),
    xml2js: require('xml2js'),
};

var Me2SexImages = {

    /**
     * 配置选项
     */
    options: {
        // 网站地址
        // 整站下载uri须如下填写
        // uri: 'http://me2-sex.lofter.com/',
        uri: 'http://me2-sex.lofter.com/tag/美女摄影',
        // 保存到此文件夹
        saveTo: '/Users/Fay/Pictures/me2sex',
        // 从第几页开始下载
        startPage: 1,
        // 图片并行下载上限
        downLimit: 5,
        /**
         * 每篇文章对应本地文件夹的目录名称格式，使用ejs渲染，支持的变量有：
         * id: 文章的id，如：2c9c66_bf1c3b。(http://me2-sex.lofter.com/post/2c9c66_bf1c3b)
         * loc: 文章的url，如：http://me2-sex.lofter.com/post/2c9c66_bf1c3b
         * lastmod: 文章的最后修改时间，2013-12-16
         * title: 文章的标题，如：'ula 安静'
         */
        postFolerFormat: '<%= id %>-<%= title %>'
    },

    posts: [],

    /**
     * 开始下载（程序入口函数）
     */
    start: function() {
        var self = this;
        var async = node.async;

        async.waterfall([
            self.wrapTask(self.getPages),
            self.wrapTask(self.downAllImages),
        ], function(err, result) {
            if (err) {
                console.log('error: %s', err.message);
            } else {
                console.log('success: 下载成功');
            }
        });
    },

    /**
     * 包裹任务，确保原任务的上下文指向某个特定对象
     * @param  {Function} task 符合async.js调用方式的任务函数
     * @param  {Any} context 上下文
     * @param  {Array} exArgs 额外的参数，会插入到原task参数的前面
     * @return {Function} 符合async.js调用方式的任务函数
     */
    wrapTask: function(task, context, exArgs) {
        var self = this;
        return function() {
            var args = [].slice.call(arguments);
            args = exArgs ? exArgs.concat(args) : args;
            task.apply(context || self, args);
        };
    },

    /**
     * 爬取所有文章列表页
     */
    getPages: function(callback) {
        var self = this;
        var async = node.async;
        var i = self.options.startPage || 1;
        async.doWhilst(
            function(callback) {
                var uri = self.options.uri + '?page=' + i;
                i++;
                async.waterfall([
                    self.wrapTask(self.downPage, self, [uri]),
                    self.wrapTask(self.parsePage)
                ], callback);
            },
            function(postsNum) {
                return postsNum > 0;
            },
            callback
        );
    },

    /**
     * 下载单个页面
     */
    downPage: function(uri, callback) {
        console.log('开始下载页面：%s', uri);
        node.request(encodeURI(uri), function(err, res, body) {
            if (!err) console.log('下载页面成功：%s', uri);
            var page = {uri: uri, html: body};
            callback(err, page);
        });
    },

    /**
     * 解析单个页面并获取数据
     */
    parsePage: function(page, callback) {
        console.log('开始分析页面妹子数据：%s', page.uri);
        var self = this;
        var $ = node.cheerio.load(page.html);
        var $posts = $('.m-post');

        $posts.each(function() {
            var href = $(this).find('.img').attr('href');
            var url = node.url.parse(href);
            self.posts.push({
                id: node.path.basename(url.pathname),
                loc: href,
                lastmod: $(this).find('.date').text(),
                title: $(this).find('.text').text()
            });
        });
        console.log('分析页面妹子数据成功，共%d篇', $posts.length);
        callback(null, $posts.length);
    },

    /**
     * 下载整站图片
     */
    downAllImages: function(callback) {
        var self = this;
        var async = node.async;
        console.log('开始全力下载所有妹子图片，共%d篇', self.posts.length);
        async.eachSeries(self.posts, self.wrapTask(self.downPostImages), callback);
    },


    /**
     * 下载单个post的图片
     * @param  {Object} post 文章
     */
    downPostImages: function(post, callback) {
        var self = this;
        var async = node.async;

        async.waterfall([
            self.wrapTask(self.mkdir, self, [post]),
            self.wrapTask(self.getPost),
            self.wrapTask(self.parsePost),
            self.wrapTask(self.downImages),
        ], callback);
    },

    mkdir: function(post, callback) {
        var path = node.path;
        var postFolder = node.ejs.render(this.options.postFolerFormat, post);
        post.dir = path.join(this.options.saveTo, postFolder);

        console.log('准备创建目录：%s', post.dir);
        if (node.fs.existsSync(post.dir)) {
            callback(null, post);
            console.log('目录：%s 已经存在', post.dir);
            return;
        }
        node.mkdirp(post.dir, function(err) {
            callback(err, post);
            console.log('目录：%s 创建成功', post.dir);
        });
    },

    /**
     * 获取post内容
     */
    getPost: function(post, callback) {
        console.log('开始请求页面：%s', post.loc);
        node.request(encodeURI(post.loc), function(err, res, body) {
            if (!err) post.html = body;
            callback(err, post);
            console.log('请求页面成功：%s', post.loc);
        });
    },

    /**
     * 解析post，并获取post中的图片列表
     */
    parsePost: function(post, callback) {
        var $ = post.$ = node.cheerio.load(post.html);
        post.images = $('.img')
            .map(function() {return $(this).attr('bigimgsrc');})
            .toArray();
        callback(null, post);
    },

    /**
     * 下载post图片列表中的图片
     */
    downImages: function(post, callback) {
        console.log('发现%d张妹子图片，准备开始下载...', post.images.length);
        node.async.eachLimit(
            post.images,
            this.options.downLimit,
            this.wrapTask(this.downImage, this, [post]),
            callback
        );
    },

    /**
     * 下载单个图片
     */
    downImage: function(post, imgsrc, callback) {
        var url = node.url.parse(imgsrc);
        var fileName = node.path.basename(url.pathname);
        var toPath = node.path.join(post.dir, fileName);
        console.log('开始下载图片：%s，保存到：%s，文件名：%s', imgsrc, post.dir, fileName);
        node.request(encodeURI(imgsrc))
            .pipe(node.fs.createWriteStream(toPath))
            .on('close', function() {
                console.log('图片下载成功：%s', imgsrc);
                callback();
            })
            .on('error', callback);
    }
};

Me2SexImages.start();