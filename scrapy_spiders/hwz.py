from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors.sgml import SgmlLinkExtractor
from scrapy.selector import HtmlXPathSelector

# from nypbot.utils import normalizeFriendlyDate


from dateutil.parser import parse

class HwzSpider(CrawlSpider):
    name = "hwz"
    allowed_domains = ["hardwarezone.com.sg"]
    start_urls = [
        "http://forums.hardwarezone.com.sg/current-affairs-lounge-17/"
    ]
    rules = (
        # Extract links matching 'garage-sales-18/.*html' (but not matching 'subsection.php')
        # and follow links from them (since no callback means follow=True by default).
        # Rule(SgmlLinkExtractor(allow=('garage\-sales\-18/.*\.html', )), callback='parse_item', follow=True),
        Rule(SgmlLinkExtractor(allow=('current\-affairs\-lounge\-17/.*\.html', )), callback='parse_item', follow=True),

        # Extract links matching 'item.php' and parse them with the spider's method parse_item
        #Rule(SgmlLinkExtractor(allow=('item\.php', )), callback='parse_item'),
    )


    def insert_posts(self,posts):
        return
	
    """
    When writing crawl spider rules, avoid using parse as callback, since the CrawlSpider uses the parse method itself to implement its logic. So if you override the parse method, the crawl spider will no longer work.
    """

    def parse_item(self, response):
        hxs = HtmlXPathSelector(response)
        posts = hxs.select("//div[@id='posts']/div[@class='post-wrapper']")
        items = []

        for post in posts:
            item = {}
            item['author_id'] = ''.join(post.select(".//a[@class='bigusername']/text()").extract())
            item['url'] = response.url
            item['body'] = '\n'.join(map(lambda x:x.strip('\t\n\r'),post.select(".//td[@class='alt1']/div/text()").extract()))
            item['title'] = '\n'.join(map(lambda x:x.strip('\t\n\r'),post.select("//h2[@class='header-gray']/text()").extract()))
            item['date_posted'] = ''.join(map(lambda x:x.strip(' \t\n\r#').strip(),post.select(".//td[@class='thead']/text()").extract())) # todo: deal with Today and Yesterday
            # item['date_posted'] = normalizeFriendlyDate(' '.join(map(lambda x:x.strip(' \t\n\r'),post.select(".//td[@class='thead']/text()").extract()))) # todo: deal with Today and Yesterday
            items.append(item)
        # self.insert_posts(items)
        print(items)
        return items
