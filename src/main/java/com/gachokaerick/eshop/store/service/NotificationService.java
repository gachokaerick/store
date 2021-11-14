package com.gachokaerick.eshop.store.service;

import com.gachokaerick.eshop.store.domain.Notification;
import com.gachokaerick.eshop.store.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Save a notification.
     *
     * @param notification the entity to save.
     * @return the persisted entity.
     */
    public Mono<Notification> save(Notification notification) {
        log.debug("Request to save Notification : {}", notification);
        return notificationRepository.save(notification);
    }

    /**
     * Partially update a notification.
     *
     * @param notification the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Notification> partialUpdate(Notification notification) {
        log.debug("Request to partially update Notification : {}", notification);

        return notificationRepository
            .findById(notification.getId())
            .map(existingNotification -> {
                if (notification.getDate() != null) {
                    existingNotification.setDate(notification.getDate());
                }
                if (notification.getDetails() != null) {
                    existingNotification.setDetails(notification.getDetails());
                }
                if (notification.getSentDate() != null) {
                    existingNotification.setSentDate(notification.getSentDate());
                }
                if (notification.getFormat() != null) {
                    existingNotification.setFormat(notification.getFormat());
                }

                return existingNotification;
            })
            .flatMap(notificationRepository::save);
    }

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Notification> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of notifications available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return notificationRepository.count();
    }

    /**
     * Get one notification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Notification> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id);
    }

    /**
     * Delete the notification by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        return notificationRepository.deleteById(id);
    }
}
