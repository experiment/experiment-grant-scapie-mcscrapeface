import re
import pudb
import urllib
import os
import glob
from subprocess import call
from spencer import cook_soup, str_link_builder

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



print full_links
