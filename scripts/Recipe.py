import nltk
import re

pnum = re.compile('[(]?\d+[\)\.\:]\s*')
pdate = re.compile('\d+/\d+(/\d+)?\s\d+\:\d+')
pstartfrom = re.compile('\W*((from)|(source)|(revised)'+\
                        '|(posted)|(recipe)|(busted)|(file))\:?\s',re.I)
def valid_instruction(text):
    if not pnum.match(text) is None:
        print 'rejected '+text
        return False
    if not pdate.search(text) is None:
        print 'rejected '+text
        return False
    if not pstartfrom.match(text) is None:
        print 'rejected '+text
        return False
    return True

replacements = [['\n',''],
                ['\r',''],
                ['tsp.','teaspoon'],
                ['tbsp.','tablespoon'],
                ['tb.','tablespoon'],
                ['min.','minute'],
                ['c.','cup'],
                ['mod.','moderate']]

class Recipe:
    def __init__(self,title,rel_url):
        self.title = title
        self.rel_url = rel_url
        self.categories = []
        self.ingredients = []
        self.steps = []

    def set_yield(self,amount):
        self.servings = amount

    def add_category(self,category):
        self.categories.append(category)

    def add_ingredient(self,ingredient):
        self.ingredients.append(ingredient)

    def add_instructions(self,textblock):
        self.steps = nltk.tokenize.sent_tokenize(textblock)
        for r in replacements:
            self.steps = [s.replace(r[0],r[1]) for s in self.steps]
        self.steps = [s for s in self.steps if valid_instruction(s)]

    def __str__(self):
        result = self.title + ' - '+self.rel_url + '\n'
        result += str(self.servings) + ' servings\n'
        result += 'Ingredients:\n'
        for i in self.ingredients:
            result += '\t'+i.__str__()
        result += 'Steps:\n'
        cur = 1
        for s in self.steps:
            result += '\t'+str(cur) + ') '+s + '\n'
            cur += 1
        return result

simplify_table = [[['c','cups','cup'],'c'],
                  [['teaspoon','teaspoons','ts'],'ts'],
                  [['tablespoon','tablespoons','tb'],'tb'],
                  [[' '],'']]

breaks = [',',';','or','.']

def measure_convert(text):
    if text is None:
        return 0
    res = 0.0
    parts = text.strip().split(' ')
    for p in parts:
        if '/' in p:
            [num,den] = p.split('/')
            res += float(num)/float(den)
        else:
            res += float(p)
    return res
            
class Ingredient:
    def __init__(self,prop_dict):
        self.ingredient = prop_dict['ingredient'].lower()
        self.unit = prop_dict['unit']
        self.measure = measure_convert(prop_dict['measure'])
        self.alt_unit = prop_dict['alt_unit']
        self.alt_measure = measure_convert(prop_dict['alt_measure'])
        self.simplify_ingredient()
        self.split_ingredient()

    def simplify_ingredient(self):
        if not self.unit is None:
            self.unit = self.unit.lower()
        if not self.alt_unit is None:
            self.alt_unit = self.alt_unit.lower()
        for row in simplify_table:
            if self.unit in row[0]:
                self.unit = row[1]
            if self.alt_unit in row[0]:
                self.unit = row[1]

    def split_ingredient(self):
        ing = []
        detail = []
        first = True
        tokens = nltk.tokenize.word_tokenize(self.ingredient)
        for t in tokens:
            if t in breaks:
                first = False
            if first:
                if t in breaks:
                    first = False
                else:
                    ing.append(t)
            else:
                detail.append(t)
        if len(detail) > 1 and detail[0] in [',',';','.']:
            detail = detail[1:]
        self.ingredient = ' '.join(ing)
        self.detail = ' '.join(detail)

    def __str__(self):
        result = str(self.measure)+' '+self.unit+' '+self.ingredient+' '
        result += '('+self.detail+')'
        if self.alt_unit != None:
            result += '('+str(self.alt_measure)+' '+self.alt_unit+')'
        result += '\n'
        return result
