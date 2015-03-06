//依赖模块
var fs = require('fs');
var request = require("request");
var cheerio = require("cheerio");
var mkdirp = require('mkdirp');
var http = require('http');
var urlparse = require('url').parse;
 
//目标网址
var url = 'http://me2-sex.lofter.com/tag/美女摄影?page=';
 
//本地存储目录
var dir = './images';
 
//创建目录
mkdirp(dir, function (err) {
    if (err) {
        console.log(err);
    }
});
 
function sleep(milliSeconds) {
    var startTime = new Date().getTime();
    while (new Date().getTime() < startTime + milliSeconds);
};
 
 
//发送请求
var getFile = function (url, i, max) {
    if (i > max)return;
    request(url + i, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var $ = cheerio.load(body);
            $('.img img').each(function () {
                var src = $(this).attr('src');
                getDetailFile($(this).parent().attr('href'));
                console.log('正在下载' + src);
                var name = src.match('[^=/]\\w*\\.jpg\\w*')[0];
                saveImg(src,dir,  name);
             //   sleep(2000);
            });
            i++;
            getFile(url,i,max);
 
        }
    });
};
 
var getDetailFile = function (url){
    request(url, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var $ = cheerio.load(body);
            $('.img img').each(function () {
                var src = $(this).attr('src');
                console.log('正在下载detail' + src);
                saveImg(src,dir,  Math.floor(Math.random() * 100000) + src.substr(-4, 4));
 
            });
        }
    });
}
 
 
 
 
function saveImg(url, dir,name){
    sleep(500);
    http.get(url, function(res){
        res.setEncoding('binary');
        var data='';
        res.on('data', function(chunk){
            data+=chunk;
        });
        res.on('end', function(){
            fs.writeFile(dir + "/"+name, data, 'binary', function (err) {
                if (err) throw err;
                console.log('file saved '+name);
            });
        });
    }).on('error', function(e) {
        console.log('error'+e)
    });
}
var i = 1;
getFile(url, i, 100);
