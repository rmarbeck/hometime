            google.load("feeds", "1");
            
            function Truncate(str, maxLength, suffix)
            {
                if(str.length > maxLength)
                {
                    str = str.substring(0, maxLength + 1); 
                    str = str.substring(0, Math.min(str.length, str.lastIndexOf(" ")));
                    str = str + suffix;
                }
                return str;
            }
        
            function initialize() {
              var feed = new google.feeds.Feed("https://www.watchnext.fr/blog/category/rolex/feed/");
              var rssoutput="";
              feed.setNumEntries(8);
              feed.load(function(result) {
                if (!result.error) {
                  var container = document.getElementById("feeddiv2");
                  for (var i = 0; i < result.feed.entries.length; i++) {
                    var entry = result.feed.entries[i];
                    rssoutput+=" &nbsp;&gt;&nbsp; <a href='" + entry.link + "' target='_blank' title='" + entry.title + "'> Article blog : " + Truncate(entry.title, 90, "...") + "</a>"
                    rssoutput+="<br />"
                    container.innerHTML=rssoutput
                  }
                }
              });
            }
            google.setOnLoadCallback(initialize);