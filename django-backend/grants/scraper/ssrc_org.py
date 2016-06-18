# from grants.scraper.spencer import cook_soup
# from spencer import cook_soup
import re

'''
not needed when chsnged
'''
import requests
import bs4
import pudb

def cook_soup(link):
    """
    takes a link as a string
    returns a bs soup object
    """
    response = requests.get(link)
    return bs4.BeautifulSoup(response.text)

def get_link_from_soup_tag(tag):
    return tag.a['href']

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
    return soup.findAll(element, text=re.compile(text))

def find_first_element_with_text(soup, element, text):
    """
    takes
    a soup object
    and
    an element as a string, something like 'a'
    and
    a text string, something like 'print as a PDF'
    and returns a object
    which is a bs4.element.Tag object
    """
    return soup.find(element, text=re.compile(text))

def find_next_sibling_thats_element(soup, element_to_find, start_element):
    while True:
        nextNode = start_element.nextSibling
        try:
            tag_name = nextNode.name
        except AttributeError:
            tag_name = ""
        if tag_name == element_to_find:
            return nextNode.string
        else:
            break


# get http://www.ssrc.org/fellowships/
soup = cook_soup("http://www.ssrc.org/fellowships/")

# find "All Fellowships and Prizes"
fellowship_header = find_first_element_with_text(soup=soup, element='h3', text='All Fellowships')

# Find all links underneath this header
ul = fellowship_header.findNext('ul')
lis = ul.findAll('li')
links = [get_link_from_soup_tag(li) for li in lis]


print links
# pu.db
# foo = 'bar'
# bar = 'baz'