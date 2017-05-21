package nl.first8.hu.ticketsale.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
@Transactional
public class SalesResource {

    private final SalesService service;

    @Autowired
    public SalesResource(SalesService service) {
        this.service = service;
    }

    @PostMapping(path = "/ticket")
    public ResponseEntity<Long> post(@RequestParam("account_id") final Long accountId, @RequestBody final Ticket requestTicket) {
        try {
            Ticket ticket = service.insert(accountId, requestTicket);
            return ResponseEntity.ok(ticket.getId());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(path = "/ticket")
    public ResponseEntity<List<TicketDto>> getById(@RequestParam("account_id") final Long accountId) {
        try {
            List<Ticket> tickets = service.getById(accountId);
            List<TicketDto> responseTickets = tickets.stream()
                    .map(t -> new TicketDto(t.getId(), t.getArtist(), t.getGenre(), t.getLocation()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseTickets);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
