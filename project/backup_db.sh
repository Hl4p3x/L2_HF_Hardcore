#/bin/bash
mkdir -p ~/backup/
cd ~/backup/

mysqldump -u backup l2jgs > l2jgs.dump || echo 'Failed to backup l2jgs'
mysqldump -u backup l2jls > l2jls.dump || echo 'Failed to backup l2jls'
mysqldump -u backup l2web > l2web.dump || echo 'Failed to backup l2web'
mysqldump -u backup forum > forum.dump || echo 'Failed to backup forum'

backup_file_name=backup-$(date +%Y-%m-%d).tar.gz

tar -czvf $backup_file_name *.dump
scp -i ~/.ssh/$BACKUP_KEY -o User=$BACKUP_USER $backup_file_name $BACKUP_HOST:$backup_file_name