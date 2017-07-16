# BigFileUpload

## Introduction
To complete a project which can upload files continuously by blocks.
For now, this project is just a demo, if you have any advice and questions, please feel free to contact me.
Email: wuhua3090911074@163.com

## Problems met
+ Q: new FormData(file) and XMLHttpRequest.send(formdata), the post-end servlet accept data by inputstream, but the image data writed down in disk can't be opened caused by the data format incorrect.
+ A: XMLHttpRequest.send(file), the image accepted can be opened.

## Reffrence:
+ [HTML5文件上传组件的深度剖析](http://fex.baidu.com/blog/2014/04/html5-uploader/)
+ [Pure HTML5 file upload](https://www.script-tutorials.com/pure-html5-file-upload/)