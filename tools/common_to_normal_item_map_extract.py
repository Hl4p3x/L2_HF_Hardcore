import file_helper as fh
import lxml.etree as ET

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/stats/items'), '.xml')

masterwork_item_ids = []
masterwork_to_normal_map = {}
with open('../l2j_datapack/dist/game/data/recipes.xml') as recipe_file:
    dom = ET.parse(recipe_file)
    items = dom.findall('.//item')
    for item in items:
        regular_element = item.find('.//production')
        masterwork_element = item.find('.//productionRare')
        if masterwork_element is not None and masterwork_element.get('id') != regular_element.get('id'):
            masterwork_id = masterwork_element.get('id')
            masterwork_item_ids.append(masterwork_id)
            print('[', masterwork_id, ']', 'is going to be mapped as masterwork')
            masterwork_to_normal_map[masterwork_id] = regular_element.get('id')

print("Masterwork item ids:", masterwork_item_ids)

normal_items = {}
common_items = []
pseudo_masterwork_items = {}
for file in files_to_process:
    print("Processing file", file)
    dom = ET.parse(file)
    items = dom.findall('.//item')
    results = []
    for item in items:
        if (item.get('type') == "Armor" or item.get('type') == "Weapon"):
            item_is_common = 'Common Item - ' in item.get('name')
            item_id = item.get('id')
            if item_is_common:
                print(item.get('name'), '[', item.get('id') , ']', 'is common')
                common_items.append(item)
            elif item_id in masterwork_item_ids:
                print(item.get('name'), '[', item.get('id') , ']', 'is masterwork')
            else:
                print(item.get('name'), '[', item.get('id') , ']', 'is normal')
                normal_item = normal_items.get(item.get('name'))
                if normal_item is None:
                    normal_items[item.get('name')] = item
                else:
                    print("Found pseudo masterwork item", normal_item.get('name'), '[', normal_item.get('id'), ']')
                    if int(normal_item.get('id')) > int(item.get('id')):                    
                        pseudo_masterwork_items[normal_item.get('id')] = item.get('id')
                        normal_items[item.get('name')] = item
                    else:
                        pseudo_masterwork_items[item.get('id') ] = normal_item.get('id') 

common_to_normal_map = {}
for common_item in common_items:
    item_is_armor = common_item.get('type').lower() == 'armor'
    item_name = common_item.get('name')
    normal_item = None
    normal_item_name = item_name.replace('Common Item - ', '')
    crystal_type = common_item.get('crystal_type')
    search_name = normal_item_name
    if item_is_armor and 'Sealed' not in item_name and crystal_type in ['B', 'A', 'S']:
        search_name = 'Sealed ' + normal_item_name

    normal_item = normal_items.get(search_name)
    if normal_item is None:
        print('Could not find mapping for item', item_name, '[', common_item.get('id') , ']', 'was searching for', search_name)
    else:
        common_to_normal_map[common_item.get('id')] = normal_item.get('id')

all_map = {}
all_map.update(common_to_normal_map)
all_map.update(masterwork_to_normal_map)
all_map.update(pseudo_masterwork_items)

results = []
for key, value in all_map.items():
    results.append(key + ':' + value)

with open('common_to_normal_map.txt', 'w') as result_file:
    result_file.write(';'.join(results))
