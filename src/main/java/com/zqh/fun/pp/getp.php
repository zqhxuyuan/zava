<?php
require 'phpQuery.php';
$query = phpQuery::newDocumentFile('http://me2-sex.lofter.com/tag/美女摄影?page=1');
$companies = pq('.pic')->find('img');
foreach ($companies as $img) {
    $url = pq($img)->attr('src');
    $file = fopen($url,"r");
    $w = fopen(time().mt_rand().'.jpg',"w");
    while (!feof($file)) {
        $f = fread($file, 1024);
        fwrite($w, $f);
    }
}
