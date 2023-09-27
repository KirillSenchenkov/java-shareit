package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>, JpaSpecificationExecutor<ItemRequest> {

    @Query("select ir from ItemRequest ir where ir.requester.id <> :id order by ir.created desc")
    Page<ItemRequest> getAllCreatedByOtherOrderByCreatedDesc(Long id, Pageable page);

    List<ItemRequest> getAllByRequesterIdOrderByCreatedDesc(Long requesterId);
}
