package com.example.danceForm.Service;

import com.example.danceForm.Model.Coreografo;
import com.example.danceForm.Repository.CoreografoRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CoreografoService {

    private final CoreografoRepository coreografoRepository;

    public CoreografoService(CoreografoRepository coreografoRepository) {
        this.coreografoRepository = coreografoRepository;
    }

    public List<Coreografo> getAllCoreografi() throws IOException {
        return coreografoRepository.findAll();
    }

    public Coreografo getCoreografoById(Long id) throws IOException {
        return coreografoRepository.findById(id);
    }

    public Coreografo saveCoreografo(Coreografo coreografo) throws IOException {
        return coreografoRepository.save(coreografo);
    }
}
