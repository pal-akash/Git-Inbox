package com.akash.inbox.folders;

import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends CassandraRepository<Folder, String> {

}
