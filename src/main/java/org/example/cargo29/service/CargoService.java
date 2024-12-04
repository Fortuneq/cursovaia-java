package org.example.cargo29.service;

import java.time.LocalDate;
import java.util.List;

import org.example.cargo29.entity.Cargo;
import org.example.cargo29.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoService {

    @Autowired
    private CargoRepository repo;

    public List<Cargo> listAll(String keyword) {
        if (keyword != null) {
            return repo.search(keyword);
        }
        return repo.findAll();
    }

    public void save(Cargo cargo) {
        repo.save(cargo);
    }

    public Cargo get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Cargo> listAllSortedByDateOfArrivalAsc() {
        return repo.findAllByOrderByDateofarrivalAsc();
    }

    public List<Cargo> listAllSortedByDateOfArrivalDesc() {
        return repo.findAllByOrderByDateofarrivalDesc();
    }

    public int countCargoByDate(LocalDate date) {
        return repo.countCargoByDate(date);
    }
}
