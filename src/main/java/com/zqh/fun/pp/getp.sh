#!/bin/bash
 
function get_images() {
    local prefix_url=$1
    local url_end=$2
    local dir_name=$3
  
    for i in `seq $url_end` ; do
        wget -c -t100 -T30 -P $dir_name `printf "${prefix_url}/images/%03d.jpg" $i`
    done
}
  
get_images "http://xz1.mm667.com/xz61"  117 "xz61"
get_images "http://xz2.mm667.com/xz06" 53 "xz06"
get_images "http://xz1.mm667.com/xz03" 100 "xz03"
get_images "http://xz4.mm667.com/xz15" 35 "xz15"
get_images "http://xz2.mm667.com/xz08" 50 "xz08"
get_images "http://xz5.mm667.com/xz17" 37 "xz17"
get_images "http://xz1.mm667.com/xz04" 40 "xz04"
get_images "http://xz5.mm667.com/xz57" 43 "xz57"
get_images "http://xz2.mm667.com/xz26" 63 "xz26"
get_images "http://xz2.mm667.com/xz67" 38 "xz67"
get_images "http://xz4.mm667.com/xz56" 70 "xz56"

