import requests
import bs4
import pudb
import re


def get_link_from_soup_tag(tag):
    return tag.a['href']


def link_builder(tag):
    """
    takes a bs tag object
    returns the link of that li tag element
    """
    return "http://www.spencer.org" + get_link_from_soup_tag(tag)


def cook_soup(link):
    """
    takes a link as a string
    returns a bs soup object
    """
    response = requests.get(link)
    return bs4.BeautifulSoup(response.text)


def name_deadline_link(row):
    """
    takes a row from the table
    makes a tuple with (grant name, deadline, link)
    """
    cells = row.find_all("td")
    name = cells[0].get_text()
    deadline = cells[1].get_text()
    link = link_builder(cells[2])
    return {'name': name, 'deadline': deadline, 'link': link}


def special_match(strg, search=re.compile(r'[^0-9,$]').search):
    return not bool(search(strg))


def get_money_from_str(_str):
    """
    take in a str like $500,000
    and returns an int like 500000
    """
    return int(strip_money_and_comma(_str))


def strip_money_and_comma(_str):
    """
    take in a str like $500,000
    and returns a str like 500000
    """
    return _str.replace("$", "").replace(",", "")


def only_digits(_str):
    return re.sub("\D", "", _str)


def split_monies(double_money_str, c='$'):
    """
    takes a str like '$750,000$750,000'
    and returns a list like ['$750,000', '$750,000']
    """
    last_index = len(double_money_str) - 1
    index_of_second_money = double_money_str[1:last_index].index(c)
    return [double_money_str[0:index_of_second_money + 1], double_money_str[index_of_second_money + 1:last_index + 1]]


def find_money_amount(_str):
    if len(_str) == 1:
        return False
    elif _str.count('$') > 1:
        return max([get_money_from_str(sub_str) for sub_str in split_monies(_str)])
    elif not special_match(_str):
        no_money_or_comma = strip_money_and_comma(_str)
        only_numbers = int(only_digits(no_money_or_comma))
        return only_numbers
    else:
        return get_money_from_str(_str)


def find_max_grant_amount(soup):
    """
    takes a soup object
    finds all money amounts
    returns highest in soup
    """
    monies = []
    content_div_text = soup.select('div#right')[0].get_text()
    for word in content_div_text.split():
        if "$" in word:
            monies.append(find_money_amount(word))
    return max(monies)


def find_contact_email_address(soup):
    """
    takes a soup object
    finds all p tag containing "program contact"
    and returns the email address with that tag
    """
    pattern = re.compile(r'Program Contact')
    address = soup(text=pattern)[0].parent.a['href'].replace('mailto:', "")
    return address


def find_contact_name(soup):
    pattern = re.compile(r'Program Contact')
    name = soup(text=re.compile(pattern))[0].replace("Program Contact:", "").strip()
    return name


soup = cook_soup("http://www.spencer.org/apply")
rows = soup.find("table").find("tbody").find_all("tr")


# get name, deadline, and link to guidelines
grants = {}
grants['spencer'] = []
for i, row in enumerate(rows):
    if i != 0:
        grants['spencer'].append(name_deadline_link(row))


# follow link to guidelines
for grant in grants['spencer']:
    # get soup
    soup = cook_soup(grant['link'])

    # find amount
    grant['amount'] = find_max_grant_amount(soup)

    # find program contact email address
    grant['contact_email'] = find_contact_email_address(soup)

    # find program contact name
    grant['contact_name'] = find_contact_name(soup)

    # find more links
    

print grants['spencer']


"""
I used this code when I was trying to parse the page at
http://www.spencer.org/what-we-fund
later, I found the page at
http://www.spencer.org/apply
Which has a table that's easier to parse
"""
# # text of response is available at response.text
# response = requests.get('http://www.spencer.org/what-we-fund')

# # intiate soup object using bs4
# soup = bs4.BeautifulSoup(response.text)

# # parse soup object for div with id of 'left'
# left_div_grant_links = soup.select('div#left li.leaf')

# # get all links in the left div
# links = [link_builder(li) for li in left_div_grant_links]

# for link in links:
#     soup = cook_soup(link)
