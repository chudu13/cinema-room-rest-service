package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CinemaRoom {
    private short totalRows;
    private short totalColumns;
    private Seat [][]availableSeats;
    private int currentIncome;

    public CinemaRoom() {
        totalRows = 9;
        totalColumns = 9;
        availableSeats = new Seat[totalRows][totalColumns];
        currentIncome = 0;

        final short priceCheap = 8;
        final short priceExpensive = 10;
        final short range = 4;

        for (short i = 1; i <= totalRows; i++) {
            for (short j = 1; j <= totalColumns; j++) {
                availableSeats[i - 1][j - 1] = new Seat(
                        i, j, i <= range ? priceExpensive : priceCheap);
            }
        }
    }

    public Optional<Seat> checkBooked(short row, short column) {

        if (row <= 0 || row > totalRows || column > totalRows || column <= 0) {
            return Optional.empty();
        }
        return Optional.of(availableSeats[row - 1][column - 1]);
    }

    public Seat searchByToken(Ticket ticket) {
        return Arrays.stream(availableSeats)
                .flatMap(Arrays::stream)
                .filter(seat -> seat.ticket.getToken().equals(ticket.getToken()))
                .findFirst()
                .orElseThrow();
    }

    public short getTotalRows() {
        return totalRows;
    }

    public short getTotalColumns() {
        return totalColumns;
    }

    public Seat[] getAvailableSeats() {
        return Arrays.stream(availableSeats)
                .flatMap(Arrays::stream)
                .filter(Seat::isAvailable)
                .collect(Collectors.toList())
                .toArray(Seat[]::new);
    }

    @JsonIgnore
    public int getCurrentIncome() {
        return currentIncome;
    }

    public long numberAvailableSeats() {
        return Arrays.stream(availableSeats)
                .flatMap(Arrays::stream)
                .filter(Seat::isAvailable)
                .count();
    }

    public long numberPurchasedTickets() {
        return Arrays.stream(availableSeats)
                .flatMap(Arrays::stream)
                .filter(seat -> !seat.isAvailable())
                .count();
    }

    public void setTotalRows(short totalRows) {
        this.totalRows = totalRows;
    }

    public void setTotalColumns(short totalColumns) {
        this.totalColumns = totalColumns;
    }

    public void buyTicket(int price, Seat seat) {
        this.currentIncome += price;
        seat.setAvailable(false);
    }

    public void returnTicket(int price, Seat seat) {
        this.currentIncome -= price;
        seat.setAvailable(true);
    }

    public void setAvailableSeats(Seat[][] availableSeats) {
        this.availableSeats = availableSeats;
    }

    static class Seat {
        private short row;
        private short column;
        private short price;
        private boolean available;
        private Ticket ticket;


        public Seat(short row, short column, short price) {
            this.row = row;
            this.column = column;
            this.price = price;
            this.available = true;
            this.ticket = new Ticket();
        }

        public Seat() {
        }

        public short getRow() {
            return row;
        }

        public short getColumn() {
            return column;
        }

        public short getPrice() {
            return price;
        }

        @JsonIgnore
        @JsonProperty(value = "available")
        public boolean isAvailable() {
            return available;
        }

        @JsonIgnore
        public Ticket getTicket() {
            return ticket;
        }

        public void setRow(short row) {
            this.row = row;
        }

        public void setColumn(short column) {
            this.column = column;
        }

        public void setPrice(short price) {
            this.price = price;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public void setTicket(Ticket ticket) {
            this.ticket = ticket;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Seat)) return false;
            Seat seat = (Seat) o;
            return Objects.equals(getTicket(), seat.getTicket());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTicket());
        }
    }

    static class Ticket {
        private UUID token;

        public Ticket() {
            this.token = UUID.randomUUID();
        }

        public UUID getToken() {
            return token;
        }

        public void setToken(UUID token) {
            this.token = token;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Ticket)) return false;
            Ticket ticket = (Ticket) o;
            return Objects.equals(getToken(), ticket.getToken());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getToken());
        }
    }
}
