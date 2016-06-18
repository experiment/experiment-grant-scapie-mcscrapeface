# from grants.scraper.spencer import cook_soup
# from spencer import cook_soup
import re

'''
not needed when chsnged
'''
import requests
import bs4
import pudb


'''
    my_grant['name'] = my_json["opportunityTitle"]
    my_grant['contact_info_email'] = my_json["synopsis"]["agencyContactEmail"]
    my_grant['contact_info_phone'] = my_json["synopsis"]["agencyPhone"]
    my_grant['contact_info_name'] = my_json["synopsis"]["agencyContactName"]
    my_grant['link'] = my_json["synopsis"]["fundingDescLinkUrl"]
    my_grant['deadline'] = my_json["synopsis"]["responseDate"]
    my_grant['description'] = my_json["synopsis"]["synopsisDesc"]
    my_grant['amount'] = my_json["synopsis"]["awardCeiling"]
    my_grant["type"] = "grant"
'''

PHONE = ""

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


def find_first_instance_of_text(soup, text):
    """
    takes
    a soup object
    and a string
    returns
    a bs4.element.Tag object
    that contains first instance of that text
    """
    return soup.find(text=re.compile(text))


def pull_number_from_element(navstr):
    phone = []
    for i, char in enumerate(list(navstr)):
        if navstr[i].isdigit():
            while True:
                if navstr[i] != ' ':
                    phone.append(navstr[i])
                    i += 1
                else:
                    return ''.join(phone)
        
    return None



def clean_non_grants(li):
    grants = ['fellowship', 'grant']
    text = li.text.lower()
    for grant in grants:
        if grant in text:
            return True
    return False

def pull_word_given_substing(substring, navstr):
    for word in navstr.split(" "):
        if substring in word:
            return word
    return None


def get_phone(grant_soup):
    global PHONE
    try:
        phone = pull_number_from_element(grant_soup.find(text=re.compile('Tel:')))
        PHONE = phone
        return phone
    except TypeError:
        return PHONE


def get_href_from_a(a):
    try:
        return a['href']
    except:
        return None

def get_staff(grant_soup):
    # get all links
    lis = grant_soup.findAll('a')
    # get all hrefs for links
    hrefs = [get_href_from_a(li) for li in lis]
    # see if href contains http://www.ssrc.org/staff/
    staff_link = ""
    for href in enumerate(hrefs):
        try:
            if "http://www.ssrc.org/staff/" in href:
                staff_link = href
        except:
            pass
    # return that link
    # go to that page
    if staff_link != "":
        staff_soup = cook_soup(staff_link)
        email = pull_word_given_substing('@ssrc.org', find_first_instance_of_text(grant_soup, '@ssrc.org'))
    else:
        return ""
    # find @ssrc.org
    # return that email


def get_email(grant_soup):
    try:
        return pull_word_given_substing('@ssrc.org', find_first_instance_of_text(grant_soup, '@ssrc.org'))
    except AttributeError:
        return get_staff(grant_soup)




# get http://www.ssrc.org/fellowships/
soup = cook_soup("http://www.ssrc.org/fellowships/")

# find "All Fellowships and Prizes"
fellowship_header = find_first_element_with_text(soup=soup, element='h3', text='All Fellowships')

# Find all links underneath this header
ul = fellowship_header.findNext('ul')
lis = ul.findAll('li')
only_grants = [li for li in lis if clean_non_grants(li)]
links = [get_link_from_soup_tag(li) for li in only_grants]

for link in links:
    # go to link
    grant_soup = cook_soup(link)
    grant = {}
    grant['name'] = grant_soup.title
    grant['contact_info_email'] = get_email(grant_soup)
    grant['contact_info_phone'] = get_phone(grant_soup)
    print grant



    

print links
# pu.db
# foo = 'bar'
# bar = 'baz'