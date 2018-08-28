import file_helper as fh
import lxml.etree as ET

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/stats/items'), '.xml')


all_items = {}
for file in files_to_process:
    print("Processing file", file)
    dom = ET.parse(file)
    items = dom.findall('.//item')
    for item in items:
        all_items[item.get('name')] = item.get('id')


results = []
for common_item_name, common_item_id in all_items.items():
    if "Common Item -" not in common_item_name:
        continue

    regular_item_name = common_item_name.replace("Common Item - ", "")
    regular_item_id = all_items.get(regular_item_name)
    if regular_item_id:
        print('Mapping', regular_item_name, 'to', common_item_name)
        results.append(common_item_id + ":" + regular_item_id)
    else:
        print(common_item_name, "has no corresponding regular item")

with open('common_to_normal_map.txt', 'w') as result:
    result.write(';'.join(results))
