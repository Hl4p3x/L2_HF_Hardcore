import file_helper as fh
import lxml.etree as ET

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/stats/npcs'), '.xml')

common_id_to_normal_map = {}
with open('common_to_normal_map.txt', 'r') as item_map:
    common_to_normal_raw_map = item_map.read()
    for map_item in common_to_normal_raw_map.split(";"):
        common_id, normal_id = map_item.split(":")
        common_id_to_normal_map[common_id] = normal_id


for file in files_to_process:
    print("Processing file", file)
    dom = ET.parse(file)
    drop_lists = dom.findall('.//dropLists')
    results = []
    for drop_list in drop_lists:
        items = drop_list.findall('.//item')
        for item in items:
            item_id = item.get('id')
            normal_item_id = common_id_to_normal_map.get(item_id)
            if normal_item_id:
                item.set('id', normal_item_id)
                comment_sibling = item.getnext()
                if comment_sibling is not None and comment_sibling.tag is ET.Comment:
                    if comment_sibling.text:
                        comment_sibling.text = comment_sibling.text.replace('Common Item - ', '')
                results.append(item)    

    if results:
        print("Updating", file)
        with open(file, 'wb') as f:
            f.write(ET.tostring(dom))