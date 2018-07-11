import sys
import file_helper as fh
from xml.dom import minidom

files_to_process = fh.filter_files_by_suffix(fh.files_to_process('../l2j_datapack/dist/game/data/stats/npcs'), '.xml')

results = []
for item in files_to_process:
    dom = minidom.parse(item)
    npcs = dom.getElementsByTagName('npc')
    for npc in npcs:
        if 'L2RaidBoss' == npc.getAttribute('type'):
            print('Raidboss', npc.getAttribute('id'),
                  '[', npc.getAttribute('name'), ']')
            results.append("INSERT INTO raidboss_name (id, boss_id, name, level) VALUES ({}, {}, \"{}\", {});".format(npc.getAttribute('id'), npc.getAttribute('id'), npc.getAttribute('name'), npc.getAttribute('originalLevel')))

with open('populate_rb_names.sql', 'w') as result:
    result.write('\n'.join(results))
