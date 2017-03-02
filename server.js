var express = require('express');
var request = require("request");
var cheerio = require("cheerio");

var app = express();


app.listen(process.env.PORT || 5000, function () {
  console.log('start!');
});

var Community = function(key, name, link) {
  this.key = key;
  this.name = name;
  this.link = link;
};

var CommunityList = [];
CommunityList.push(new Community('clien', '클리앙', 'https://communityall.herokuapp.com/clien/1'));
CommunityList.push(new Community('ruliweb', '루리웹', 'https://communityall.herokuapp.com/ruliweb/1'));

app.get('/', function (req, res) {
  res.send('community!');
});


app.get('/list', function(req, res) {
  res.contentType('application/json');
  res.send(JSON.stringify(CommunityList));
});


// 클리앙 모두의 공원
app.get('/clien/:page', function(req, res) {
  var page = req.params.page;
  var recent_url = null;
  var next_url = null;

  if(page == 1) {
    recent_url = "http://www.clien.net/cs2/bbs/board.php?bo_table=park";
    next_url = "http://www.clien.net/cs2/bbs/board.php?bo_table=park&page=2";
  } else {
    recent_url = "http://www.clien.net/cs2/bbs/board.php?bo_table=park&page="+page;
    next_url = "http://www.clien.net/cs2/bbs/board.php?bo_table=park&page="+(parseInt(page)+1);
  }

  console.log("recent_url : " + recent_url);
  console.log("next_url : " + next_url);

  request(recent_url, function(error, response, body) {
    var result = [];
    var list = [];
    if (error) throw error;

    var $ = cheerio.load(body);

    $("tbody .mytr").each(function() {

      var title = $(this).find("td").eq(1).find("a").text().trim();
      var link = $(this).find("td").eq(1).find("a").attr("href").trim();
      var username = $(this).find("td").eq(2).find(".member").text().trim();
      var regdate = $(this).find("td").eq(3).find("span").attr("title").trim();
      var viewcnt = $(this).find("td").eq(4).text().trim();
      var commentcnt = $(this).find("td").eq(1).find("span").text().trim();

      list.push({title:title, link:link, username:username, regdate:regdate, viewcnt:viewcnt, commentcnt:commentcnt});
    });

    result.push({recent_url:recent_url, next_url:next_url, list:list});
    res.contentType('application/json');
    res.send(JSON.stringify(result));
  });
});


// 루리웹 베스트
app.get('/ruliweb/:page', function(req, res) {
  var page = req.params.page;
  var recent_url = null;
  var next_url = null;

  if(page == 1) {
    recent_url = "http://bbs.ruliweb.com/best";
    next_url = "http://bbs.ruliweb.com/best?&page=2";
  } else {
    recent_url = "http://bbs.ruliweb.com/best?&page="+page;
    next_url = "http://bbs.ruliweb.com/best?&page="+(parseInt(page)+1);
  }

  console.log("recent_url : " + recent_url);
  console.log("next_url : " + next_url);

  request(recent_url, function(error, response, body) {
    var result = [];
    var list = [];
    if (error) throw error;

    var $ = cheerio.load(body);

    $("tbody .table_body").each(function() {

      var title = $(this).find(".subject a").text().trim();
      var link = $(this).find(".subject a").attr("href").trim();
      var username = $(this).find(".writer").text().trim();
      var regdate = $(this).find(".time").text().trim();
      var viewcnt = $(this).find(".hit").text().trim();
      var commentcnt = $(this).find(".recomd").text().trim();

      list.push({title:title, link:link, username:username, regdate:regdate, viewcnt:viewcnt, commentcnt:commentcnt});
    });

    result.push({recent_url:recent_url, next_url:next_url, list:list});
    res.contentType('application/json');
    res.send(JSON.stringify(result));
  });
});
