select ch.char_name, it.* from items it left join characters ch on ch.charId = it.owner_id where it.item_id=13722;
