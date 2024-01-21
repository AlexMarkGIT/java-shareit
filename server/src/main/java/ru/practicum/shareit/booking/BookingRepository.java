package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Id(Long itemId);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllByBooker(Long userId, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 and ?2 between b.start_date and b.end_date " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllCurrentByBooker(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 and b.end_date < ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllPastByBooker(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 and b.start_date > ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllFutureByBooker(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 and b.status = 'WAITING' and b.start_date > ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllWaitingByBooker(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 and b.status = 'REJECTED' " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllRejectedByBooker(Long userId, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllByOwner(Long userId, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 and ?2 between b.start_date and b.end_date " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllCurrentByOwner(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.end_date < ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllPastByOwner(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.start_date > ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllFutureByOwner(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.status = 'WAITING' and b.start_date > ?2 " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllWaitingByOwner(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 and b.status = 'REJECTED' " +
            "order by b.start_date desc",
            nativeQuery = true)
    List<Booking> findAllRejectedByOwner(Long userId, Pageable pageable);

    @Query(value = "select b.* from bookings as b " +
            "where b.booker_id = ?1 " +
            "and b.item_id = ?2 " +
            "and b.start_date < ?3 " +
            "and b.status = 'APPROVED'",
            nativeQuery = true)
    List<Booking> findByItemAndBooker(Long userId, Long itemId, LocalDateTime now);
}
