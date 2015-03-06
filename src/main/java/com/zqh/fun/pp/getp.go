package main

import (
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"regexp"
	"strconv"
	"strings"
	"sync"
)

var urlist [4]string
var album chan string
var w sync.WaitGroup
var dir string

func main() {
	dir = "tmp/"
	err := os.Mkdir(dir, 0777)
	if err != nil {
		isexist := os.IsExist(err)
		log.Println(isexist)
	}
	album = make(chan string, 200)
	urlist = [4]string{"http://me2-sex.lofter.com/tag/%E7%BE%8E%E5%A5%B3%E6%91%84%E5%BD%B1?page=", "http://me2-sex.lofter.com/tag/%E6%AC%A7%E7%BE%8E?page=", "http://me2-sex.lofter.com/tag/%E6%A8%A1%E7%89%B9?page=", "http://me2-sex.lofter.com/tag/%E7%BE%8E%E5%AA%9B%E9%A6%86?page="}
	for _, v := range urlist {
		for i := 1; i <= 20; i++ {
			url := v + strconv.Itoa(i)
			w.Add(1)
			go GetAlbum(url)
			w.Wait()
		}
	}
}

func GetAlbum(url string) {
	data := GetUrl(url)
	body := string(data)
	part := regexp.MustCompile(`<a class="img" href="(.*)">`)
	match := part.FindAllStringSubmatch(body, -1)
	for _, v := range match {
		album <- v[1]
		w.Add(1)
		go GetItem()
	}
	w.Done()

}

func GetItem() {
	url := <-album
	defer func() {
		ret := recover()
		if ret != nil {
			log.Println(ret)
			w.Done()
		} else {
			w.Done()
		}
	}()

	data := GetUrl(url)
	if len(data) > 10 {
		body := string(data)
		part := regexp.MustCompile(`bigimgsrc="(.*)"`)
		match := part.FindAllStringSubmatch(body, -1)
		for _, v := range match {
			str := strings.Split(v[1], "/")
			length := len(str)
			source := GetUrl(v[1])
			name := str[length-1]
			file, err := os.Create(dir + name)
			if err != nil {
				panic(err)
			}
			size, err := file.Write(source)
			defer file.Close()
			if err != nil {
				panic(err)
			}
			log.Println(size)
		}
	}
}

func GetUrl(url string) []byte {
	ret, err := http.Get(url)
	if err != nil {
		log.Println(url)
		status := map[string]string{}
		status["status"] = "400"
		status["url"] = url
		panic(status)
	}
	body := ret.Body
	data, _ := ioutil.ReadAll(body)
	return data
}
