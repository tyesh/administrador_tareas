package com.tasks.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("github")
public class GitHubController {
    
    @RequestMapping(value = "{user}")
    public ResponseEntity<Object> getRepositoryFrom(@PathVariable("user") String user){
        String resp = "";

        try {
            URL url = new URL("https://api.github.com/users/" + user + "/repos");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    resp = resp + line + "\n";
                }
            }
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "popular/{sort}")
    public ResponseEntity<Object> getRepositoryPopularByDay(@PathVariable("sort") String sort){
        String resp = "";

        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formmat1;

        if(sort.equals("week")){
            ldt = ldt.minusDays(7);
        }
        else if(sort.equals("month")){
            ldt = ldt.withDayOfMonth(1);
        }
        formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
        String formatter = formmat1.format(ldt);

        try {
            URL url = new URL("https://api.github.com/search/repositories?sort=stars&q=created:" + formatter + "&order=desc&per_page=10");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    resp = resp + line + "\n";
                }
            }
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}