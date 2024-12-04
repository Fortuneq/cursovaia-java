package org.example.cargo29.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.cargo29.entity.Cargo;
import org.example.cargo29.repository.CargoRepository;
import org.example.cargo29.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AppController {

    @Autowired
    private CargoService service;

    @Autowired
    private CargoRepository repo;

    @RequestMapping("/")
    public String viewHomePage(Model model, @Param("keyword") String keyword, @Param("sort") String sort) {
        List<Cargo> listCargo;
        if (keyword != null) {
            listCargo = service.listAll(keyword);
        } else if ("new_to_old".equals(sort)) {
            listCargo = service.listAllSortedByDateOfArrivalDesc();
        } else if ("old_to_new".equals(sort)) {
            listCargo = service.listAllSortedByDateOfArrivalAsc();
        } else {
            listCargo = service.listAll(null);
        }
        model.addAttribute("listCargo", listCargo);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "index";
    }

    @RequestMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showNewCargoForm(Model model) {
        Cargo cargo = new Cargo();
        model.addAttribute("cargo", cargo);
        return "new_cargo";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String saveCargo(@ModelAttribute("cargo") Cargo cargo) {
        service.save(cargo);
        return "Груз успешно сохранен!";
    }

    @RequestMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody Cargo showEditCargoForm(@PathVariable(name = "id") Long id) {
        Cargo cargo = service.get(id);
        return cargo;
    }

    @RequestMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String deleteCargo(@PathVariable(name = "id") Long id) {
        service.delete(id);
        return "Груз успешно удален!";
    }

    @RequestMapping("/search")
    public String searchCargo(Model model, @Param("keyword") String keyword) {
        List<Cargo> resultList = service.listAll(keyword);
        model.addAttribute("listCargo", resultList);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @RequestMapping("/histogram")
    public String showHistogram() {
        return "histogram";
    }

    @RequestMapping("/histogram-data")
    public @ResponseBody List<Object[]> getHistogramData() {
        return repo.getHistogramData();
    }

    @RequestMapping("/count-cargo-by-date")
    public @ResponseBody int countCargoByDate(@Param("date") String date) {
        return service.countCargoByDate(LocalDate.parse(date));
    }
}
