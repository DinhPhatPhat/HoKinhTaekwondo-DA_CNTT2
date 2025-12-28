package com.hokinhtaekwondo.hokinh_taekwondo.repository;

import com.hokinhtaekwondo.hokinh_taekwondo.model.BotFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotFileRepository extends JpaRepository<BotFile, String> {
    Optional<BotFile> findBotFileByPythonFileId(String pythonFileId);
    List<BotFile> findBotFilesByStatus(String status);
    List<BotFile> findBotFilesByPythonFileIdIn(List<String> pythonFileIds);
    void deleteAllByPythonFileIdNotInOrPythonFileIdNull(List<String> pythonFileIds);
}
