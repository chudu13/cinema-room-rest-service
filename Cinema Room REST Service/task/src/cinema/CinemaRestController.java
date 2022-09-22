package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class CinemaRestController {

    private final CinemaRoom cinemaRoom = new CinemaRoom();

    @GetMapping("/seats")
    public CinemaRoom seats() {
        return cinemaRoom;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody CinemaRoom.Seat data) {

        Optional<CinemaRoom.Seat> seat = cinemaRoom.checkBooked(data.getRow(), data.getColumn());

        if (seat.isEmpty()) {
            return new ResponseEntity<>(
                    Map.of("error", "The number of a row or a column is out of bounds!"), HttpStatus.BAD_REQUEST);
        } else if (seat.get().isAvailable()) {
            cinemaRoom.buyTicket(seat.get().getPrice(), seat.get());
            return new ResponseEntity<>(Map.of(
                    "token", seat.get().getTicket().getToken(),
                    "ticket", seat.get()),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    Map.of("error", "The ticket has been already purchased!"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returned(@RequestBody CinemaRoom.Ticket ticket) {

        try {
            CinemaRoom.Seat seat = cinemaRoom.searchByToken(ticket);
            cinemaRoom.returnTicket(seat.getPrice(), seat);
            return new ResponseEntity<>(Map.of(
                    "returned_ticket", seat),
                    HttpStatus.OK
            );
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>(
                    Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/stats")
    public ResponseEntity<?> statistics(@RequestParam(required = false) String password) {
        String adminPassword = "super_secret";

        if (adminPassword.equals(password)) {
            return new ResponseEntity<>(Map.of(
                    "current_income", cinemaRoom.getCurrentIncome(),
                    "number_of_available_seats", cinemaRoom.numberAvailableSeats(),
                    "number_of_purchased_tickets", cinemaRoom.numberPurchasedTickets()),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(Map.of(
                    "error", "The password is wrong!"),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

}
