import urllib,os,re

baseurl = "http://www.recipes.stevex.net"

def get_categories():
    global baseurl
    f = urllib.urlopen(baseurl+"/categories")
    s = f.read()
    f.close()
    #find matches
    p = re.compile("""<a href="(/category/.+?)"/>(.+?)</a>""")
    categories = []
    for match in p.finditer(s):
        categories.append([match.group(1),match.group(2)])
    return categories

def get_recipes_for_category(rel_url):
    global baseurl
    f = urllib.urlopen(baseurl+rel_url)
    s = f.read()
    f.close()
    #find matches
    p = re.compile("""<a href="(/recipe/.+?)">(.+?)</a>""")
    recipes = []
    for match in p.finditer(s):
        recipes.append([match.group(1),match.group(2)])
    #go to next page if there is one
    p_next = re.compile("""<a href="(/category/.+?)">Next Page >></a>""")
    r = p_next.search(s)
    if not r is None:
        recipes += get_recipes_for_category(r.group(1))
    return recipes

basic_units = ['c','ts','tb','[ ]', 'tb','g','kg','ml','cups','cup','lb',
         'pk','lg','pn','sm','oz','ds','stick']
#categories
pcat = re.compile('<a href="/category/(.+?)">')
#yeild
pyield = re.compile("Yield: (\d+) Servings")
#ingredients
sp = "[ \t\f\r\v]+?"    
start='(?<=\n)'
m_core = "\d+[ ]?\d*/?\d*" #1 1/2, 2, 2/3
measure = "(?P<measure>"+m_core+")" 
u_core = '|'.join(['('+u+')' for u in basic_units])
units = '(?P<unit>'+u_core+')\.?'
ing = "(?P<ingredient>[0-9a-zA-Z]+.*?)"
opt_2 = '('+sp+'\((?P<alt_measure>'+m_core+')[\s](?P<alt_unit>'+u_core+')\))?'
end = '(?=(\n\n)|(\n[ \t\f\r]*?\d))'
pingredients = re.compile(start+'('+sp+measure+sp+units+opt_2+sp+ing+'\s+?)'+end,
                   re.DOTALL)
#instructions
pinstructions = re.compile('\s+(.*?)\s+</pre>',re.DOTALL)

def get_recipe(rel_url):
    global baseurl,pyield,pingredients,pinstructions
    f = urllib.urlopen(baseurl+rel_url)
    s = f.read()
    f.close()
    #find categories
    categories = []
    for match in pcat.finditer(s):
        categories.append(match.group(1))
    #find yield
    yld = float(pyield.search(s).group(1))
    #find ingredients
    ingredients = []
    end = 0
    for match in pingredients.finditer(s):
        ingredients.append(simplify_ing_units(match.groupdict()))
        end = match.end(0)
    #find instructions
    instructions = pinstructions.search(s,end).group(1)
    return [categories,yld,ingredients,instructions]

simplify_table = [[['c','cups','cup'],'c'],
                  [['teaspoon','teaspoons','ts'],'ts'],
                  [['tablespoon','tablespoons','tb'],'tb'],
                  [[' '],'']]
def simplify_ing_units(ing_dict):
    for row in simplify_table:
        if ing_dict["unit"] in row[0]:
            ing_dict["unit"] = row[1]
        if ing_dict["alt_unit"] in row[0]:
            ing_dict["alt_unit"] = row[1]
    return ing_dict

def ensure_dir(f):
    if not os.path.exists(f):
        os.makedirs(f)

def save_recipe(lst,category,name):
    nm = name.replace('"','').replace('*','')
    ensure_dir('recipes')
    f = open('recipes/'+nm+'.txt','w')
    f.write('<recipe category="'+','.join(lst[0])+'" name="'+nm+\
            '" yield="'+str(lst[1])+'">\n')
    f.write('\t<ingredients>\n')
    for i in lst[2]:
        f.write('\t\t<ingredient ')
        for k in i:
            if not i[k] is None:
                f.write(k+'="'+i[k]+'" ')
        f.write('/>\n')
    f.write('\t</ingredients>\n\t<instructions>'+lst[3]+\
            '</instructions>\n</recipe>')
    f.close()

def save_category(cat_url,cat_name):
    rs = get_recipes_for_category(cat_url)
    for r in rs:
        print 'getting '+r[1]
        try:
            rec = get_recipe(r[0])
            save_recipe(rec,cat_name,r[1])
        except Exception as inst:
            print inst
            print 'failed to get '+r[0]

#c = get_categories()
#save_category(c[5][0],c[5][1])
#print get_recipe('/recipe/amish_apple_brownies')
