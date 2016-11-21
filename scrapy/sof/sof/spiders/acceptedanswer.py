from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors.sgml import SgmlLinkExtractor
from scrapy.selector import HtmlXPathSelector

# from nypbot.utils import normalizeFriendlyDate


from dateutil.parser import parse


import pymysql.cursors



answers = open('answers.csv', 'a')

class AcceptedAnswerSpider(CrawlSpider):
    name = "acceptedanswer"
    allowed_domains = ["stackoverflow.com"]
    start_urls = [
        "http://stackoverflow.com/search?q=regular+expression"
    ]
    rules = (
        # Extract links matching 'garage-sales-18/.*html' (but not matching 'subsection.php')
        # and follow links from them (since no callback means follow=True by default).
        Rule(SgmlLinkExtractor(allow=('questions/[0-9]+/.*', )), callback='parse_item', follow=True),

    )


    
    def insert_item(self,item):
        connection = pymysql.connect(host='localhost',
                             user='root',
                             password='root',
                             db='stackoverflow',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

        try:
            with connection.cursor() as cursor:
                # Create a new record
		sql = "INSERT INTO `acceptedanswer` (`url`, `pre`, `time_posted`, `author`, `vote`) VALUES (%s, %s, %s, %s, %s)"
                cursor.execute(sql, (item['url'], item['pre_text'], item['time_posted'], item['author'], item['vote']))

                # connection is not autocommit by default. So you must commit to save
                # your changes.
                connection.commit()

        finally:
            connection.close()
        return
    
    """
    When writing crawl spider rules, avoid using parse as callback, since the CrawlSpider uses the parse method itself to implement its logic. So if you override the parse method, the crawl spider will no longer work.
    """

    def parse_item(self, response):
        global answers
        hxs = HtmlXPathSelector(response)
        posts = hxs.select("//div[@id='answers']/div[@class='answer accepted-answer']")
        items = []


        for post in posts:
            # print(post)
            item = {}
	    item['url'] = response.url
            item['pre_text'] = ''.join(post.select(".//div[@class='post-text']//pre//text()").extract())
            item['time_posted'] = parse(post.select(".//div[@class='user-action-time']//span/text()").extract()[0])
	    item['author']= ''.join(post.select(".//div[@class='user-details']//a/text()").extract())
	    item['vote']=''.join(post.select(".//div[@class='vote']//span[@class='vote-count-post ' or @class='vote-count-post high-scored-post']/text()").extract())



            self.insert_item(item)
            items.append(item)
        # self.insert_posts(items)
        #for item in items:
        #    print >> answers, "%s,'%s'\n" % (item['url'], item['pre_text'])
        return items










