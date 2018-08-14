import sys
import file_helper as fh
import lxml.etree as ET

script_name = sys.argv

files_to_process = fh.filter_files_by_suffix(fh.files_to_process("../l2j_datapack/dist/game/data/stats/npcs/"), '.xml')

for item in files_to_process:
    dom = ET.parse(item)
    npcs = dom.findall('.//npc')
    for npc in npcs:
        if npc.get('originalLevel'):
            npc.set('level', npc.get('originalLevel'))
            npc.attrib.pop("originalLevel", None)

    with open(item, 'wb') as f:
        f.write(ET.tostring(dom))
