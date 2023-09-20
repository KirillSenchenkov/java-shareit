package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @Query("select i from Item i where  i.available = true  and "
            + "(lower(i.name) like lower(?1) or lower(i.description) like lower(?1))")
    List<Item> findByNameOrDescription(String text);
}
