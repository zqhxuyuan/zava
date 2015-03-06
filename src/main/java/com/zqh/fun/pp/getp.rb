#encoding: utf-8
require 'rubygems'
require 'mechanize'
require 'open-uri'
Mechanize.new.get('http://me2-sex.lofter.com/tag/美女摄影?page=1').images.each do |img|
  File.open("./images/#{File.basename(img.url.to_s)}", 'w') { |f| f << open(img.url.to_s).read }
end
