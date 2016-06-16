import re
import urllib
import os
import glob
import requests
import subprocess
from spencer import cook_soup, str_link_builder, pretty_print
import json

# download all pdf's at http://ies.ed.gov/funding/17rfas.asp

# save them to a directory in experiment-scraper/

# convert all the pdf's to html using pdfminer

# parse the html to find

# "name": "Midcareer Grant Program",
# "amount": 150000,
# "contact_email": "midcareer@spencer.org",
# "link": "http://www.spencer.org/midcareer-grant-program-guidelines",
# "contact_name": "Maricelle Garcia",
# "deadline": "September 13, 2016, 4:00pm CDT",
# "description"

# additionally

# Applicant Type
# Eligibility
# Keywords
# POST /grantsws/OppsSearch HTTP/1.1
# Host: www.grants.gov
# Connection: keep-alive
# Content-Length: 63
# Accept: application/json, text/javascript, */*; q=0.01
# Origin: http://www.grants.gov
# X-Requested-With: XMLHttpRequest
# User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36
# Content-Type: application/x-www-form-urlencoded; charset=UTF-8
# Referer: http://www.grants.gov/custom/search.jsp
# Accept-Encoding: gzip, deflate
# Accept-Language: en-US,en;q=0.8
# Cookie: BIGipServerProd-Liferay-Pool=285870602.32031.0000; JSESSIONID=CC1BBA5FA67CDA9080B7EE391B3EC867; __utma=119926110.2080771840.1465935673.1465937835.1465943745.4; __utmb=119926110.3.10.1465943745; __utmc=119926110; __utmz=119926110.1465937835.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); fsr.s=%7B%22v2%22%3A-2%2C%22v1%22%3A1%2C%22rid%22%3A%22de35430-94672617-59b6-f88d-6d1c5%22%2C%22ru%22%3A%22http%3A%2F%2Fpivot.cos.com%2Ffunding_opps%2F112936%22%2C%22r%22%3A%22pivot.cos.com%22%2C%22st%22%3A%22%22%2C%22to%22%3A5%2C%22c%22%3A%22http%3A%2F%2Fwww.grants.gov%2Fweb%2Fgrants%2Fsearch-grants.html%22%2C%22pv%22%3A38%2C%22lc%22%3A%7B%22d0%22%3A%7B%22v%22%3A38%2C%22s%22%3Atrue%7D%7D%2C%22cd%22%3A0%2C%22f%22%3A1465943742691%2C%22sd%22%3A0%2C%22l%22%3A%22en%22%2C%22i%22%3A-1%7D

# curl 'http://www.grants.gov/grantsws/OppsSearch' -H 'Cookie: BIGipServerProd-Liferay-Pool=285870602.32031.0000; JSESSIONID=CC1BBA5FA67CDA9080B7EE391B3EC867; __utma=119926110.2080771840.1465935673.1465937835.1465943745.4; __utmb=119926110.3.10.1465943745; __utmc=119926110; __utmz=119926110.1465937835.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); fsr.s=%7B%22v2%22%3A-2%2C%22v1%22%3A1%2C%22rid%22%3A%22de35430-94672617-59b6-f88d-6d1c5%22%2C%22ru%22%3A%22http%3A%2F%2Fpivot.cos.com%2Ffunding_opps%2F112936%22%2C%22r%22%3A%22pivot.cos.com%22%2C%22st%22%3A%22%22%2C%22to%22%3A5%2C%22c%22%3A%22http%3A%2F%2Fwww.grants.gov%2Fweb%2Fgrants%2Fsearch-grants.html%22%2C%22pv%22%3A38%2C%22lc%22%3A%7B%22d0%22%3A%7B%22v%22%3A38%2C%22s%22%3Atrue%7D%7D%2C%22cd%22%3A0%2C%22f%22%3A1465943742691%2C%22sd%22%3A0%2C%22l%22%3A%22en%22%2C%22i%22%3A-1%7D' -H 'Origin: http://www.grants.gov' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Referer: http://www.grants.gov/custom/search.jsp' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' --data 'jp={"startRecordNum":0,"keyword":"84.305","oppStatuses":"open"}' --compressed

"""
new plan
go to 
http://ies.ed.gov/funding/17rfas.asp
get all grant names
then go to pivot
login
search for grant
find grant
find link
click link
scrape pivots page
"""

"""
go to http://www.grants.gov/web/grants/search-grants.html?keywords=84.305
scrape all js links
for each
go to http://www.grants.gov/view-opportunity.html?dpp=1&oppId= + js link id
scrape

http://www.grants.gov/view-opportunity.html?dpp=1&oppId=282041
"""

def find_all_elements_with_text(soup, element, text):
    """
    takes
    a soup object
    and
    an element as a string, something like 'a'
    and
    a text string, something like 'print as a PDF'
    and returns a list
    where each element is a bs4.element.Tag object
    """
    return soup(element, text=re.compile(text))


def split_at_paren(_str):
    """
    """

def run_post():
    url = 'http://www.grants.gov/grantsws/OppsSearch'
    data = {"startRecordNum":0,"keyword":"84.305","oppStatuses":"open"}
    headers = {'Content-Type': 'application/x-www-form-urlencoded'}

    r = requests.post(url, data=json.dumps(data), headers=headers)

    return json.dumps(r.json(), indent=4)

first_str = """
curl 'http://www.grants.gov/grantsws/OppsSearch' -H 'Cookie: BIGipServerProd-Liferay-Pool=285870602.32031.0000; JSESSIONID=CC1BBA5FA67CDA9080B7EE391B3EC867; __utma=119926110.2080771840.1465935673.1465937835.1465943745.4; __utmb=119926110.3.10.1465943745; __utmc=119926110; __utmz=119926110.1465937835.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); fsr.s=%7B%22v2%22%3A-2%2C%22v1%22%3A1%2C%22rid%22%3A%22de35430-94672617-59b6-f88d-6d1c5%22%2C%22ru%22%3A%22http%3A%2F%2Fpivot.cos.com%2Ffunding_opps%2F112936%22%2C%22r%22%3A%22pivot.cos.com%22%2C%22st%22%3A%22%22%2C%22to%22%3A5%2C%22c%22%3A%22http%3A%2F%2Fwww.grants.gov%2Fweb%2Fgrants%2Fsearch-grants.html%22%2C%22pv%22%3A38%2C%22lc%22%3A%7B%22d0%22%3A%7B%22v%22%3A38%2C%22s%22%3Atrue%7D%7D%2C%22cd%22%3A0%2C%22f%22%3A1465943742691%2C%22sd%22%3A0%2C%22l%22%3A%22en%22%2C%22i%22%3A-1%7D' -H 'Origin: http://www.grants.gov' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Referer: http://www.grants.gov/custom/search.jsp' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' --data 'jp={"startRecordNum":0,"keyword":"84.305","oppStatuses":"open"}' --compressed
"""

grant_str = """
curl 'http://www.grants.gov/grantsws/OppDetails' -H 'Cookie: JSESSIONID=088A5E1DD1A4371F19587EA777860DF9; BIGipServerProd-Liferay-Pool=269093386.32031.0000; __utmt=1; __utma=119926110.2080771840.1465935673.1465943745.1465946371.5; __utmb=119926110.2.10.1465946371; __utmc=119926110; __utmz=119926110.1465937835.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); fsr.s=%7B%22v2%22%3A-2%2C%22v1%22%3A1%2C%22rid%22%3A%22de35430-94672617-59b6-f88d-6d1c5%22%2C%22ru%22%3A%22http%3A%2F%2Fpivot.cos.com%2Ffunding_opps%2F112936%22%2C%22r%22%3A%22pivot.cos.com%22%2C%22st%22%3A%22%22%2C%22to%22%3A5%2C%22c%22%3A%22http%3A%2F%2Fwww.grants.gov%2Fview-opportunity.html%22%2C%22pv%22%3A40%2C%22lc%22%3A%7B%22d0%22%3A%7B%22v%22%3A40%2C%22s%22%3Atrue%7D%7D%2C%22cd%22%3A0%2C%22f%22%3A1465946400800%2C%22sd%22%3A0%2C%22l%22%3A%22en%22%2C%22i%22%3A-1%7D' -H 'Origin: http://www.grants.gov' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: max-age=0' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' -H 'Referer: http://www.grants.gov/custom/viewOppDetails.jsp' --data 'oppId=282062' --compressed
"""

other_grant_str_1 = """curl 'http://www.grants.gov/grantsws/OppDetails' -H 'Cookie: JSESSIONID=088A5E1DD1A4371F19587EA777860DF9; BIGipServerProd-Liferay-Pool=269093386.32031.0000; __utmt=1; __utma=119926110.2080771840.1465935673.1465943745.1465946371.5; __utmb=119926110.2.10.1465946371; __utmc=119926110; __utmz=119926110.1465937835.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); fsr.s=%7B%22v2%22%3A-2%2C%22v1%22%3A1%2C%22rid%22%3A%22de35430-94672617-59b6-f88d-6d1c5%22%2C%22ru%22%3A%22http%3A%2F%2Fpivot.cos.com%2Ffunding_opps%2F112936%22%2C%22r%22%3A%22pivot.cos.com%22%2C%22st%22%3A%22%22%2C%22to%22%3A5%2C%22c%22%3A%22http%3A%2F%2Fwww.grants.gov%2Fview-opportunity.html%22%2C%22pv%22%3A40%2C%22lc%22%3A%7B%22d0%22%3A%7B%22v%22%3A40%2C%22s%22%3Atrue%7D%7D%2C%22cd%22%3A0%2C%22f%22%3A1465946400800%2C%22sd%22%3A0%2C%22l%22%3A%22en%22%2C%22i%22%3A-1%7D' -H 'Origin: http://www.grants.gov' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Cache-Control: max-age=0' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' -H 'Referer: http://www.grants.gov/custom/viewOppDetails.jsp' --data 'oppId="""
other_grant_str_2 = """' --compressed"""


item = os.popen(first_str).read()

item = json.loads(item)

ids = []

for opp in item["oppHits"]:
    ids.append(opp["id"])


curls = []
for _id in ids:
    curl = other_grant_str_1 + _id + other_grant_str_2
    curls.append(curl)

grants = []
for curl in curls:
    out = os.popen(curl).read()
    grants.append(json.loads(out))

grant = {}
grant['ies'] = grants









# soup = cook_soup("http://www.grants.gov/web/grants/search-grants.html?keywords=84.305")



# LINK = "http://ies.ed.gov/funding/17rfas.asp"

# soup = cook_soup(LINK)

# grants = find_all_elements_with_text(soup, 'strong', '84.3')

# grants_text = [tag.get_text() for tag in grants]



# print grants_text



"""
this was my previous plan to scrape a pdf
this turned out to be essentially impossible
"""

# BASE = "http://ies.ed.gov"
# LINK = "http://ies.ed.gov/funding/17rfas.asp"
# ROOT_FILE = os.path.abspath(__file__)
# ROOT_FOLDER = os.path.abspath(os.path.join(ROOT_FILE, os.pardir))


# def find_all_elements_with_text(soup, element, text):
#     """
#     takes
#     a soup object
#     and
#     an element as a string, something like 'a'
#     and
#     a text string, something like 'print as a PDF'
#     and returns a list
#     where each element is a bs4.element.Tag object
#     """
#     return soup(element, text=re.compile(text))


# def get_link_from_a_tag_element(a_tag):
#     """
#     given a bs4.element.Tag object
#     that is an a link
#     returns the href
#     as a string
#     """
#     return a_tag['href']


# def download_pdfs(filename, _dir="/ies/pdfs/"):
#     output = ROOT_FOLDER + _dir + get_last_part_of_url(link)
#     urllib.urlretrieve(link, output)


# def convert_pdfs_in_dir_to_html(input_dir=ROOT_FOLDER + "/ies/pdfs/"):
#     os.chdir(input_dir)
#     for pdf_file in glob.glob("*.pdf"):
#         call(["pdf2txt.py", "-o", pdf_file + ".html", pdf_file])


# def get_last_part_of_url(full_url):
#     """
#     takes a url like 'http://ies.ed.gov/funding/pdf/2017_84305A.pdf'
#     as a string
#     and returns
#     '2017_84305A.pdf'
#     as a string
#     """
#     return full_url.rsplit('/', 1)[-1]


# ies_soup = cook_soup(LINK)

# link_elements = find_all_elements_with_text(soup=ies_soup, element='a', text='print as a PDF')

# link_strings = [get_link_from_a_tag_element(link) for link in link_elements]

# full_links = [str_link_builder(base=BASE, _str=link) for link in link_strings]


# """
# downloads pdfs
# converts them to html
# """
# [download_pdfs(link) for link in full_links]
# convert_pdfs_in_dir_to_html()



# print full_links
