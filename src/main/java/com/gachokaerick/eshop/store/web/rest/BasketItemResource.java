package com.gachokaerick.eshop.store.web.rest;

import com.gachokaerick.eshop.store.domain.BasketItem;
import com.gachokaerick.eshop.store.repository.BasketItemRepository;
import com.gachokaerick.eshop.store.service.BasketItemService;
import com.gachokaerick.eshop.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.gachokaerick.eshop.store.domain.BasketItem}.
 */
@RestController
@RequestMapping("/api")
public class BasketItemResource {

    private final Logger log = LoggerFactory.getLogger(BasketItemResource.class);

    private static final String ENTITY_NAME = "basketItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BasketItemService basketItemService;

    private final BasketItemRepository basketItemRepository;

    public BasketItemResource(BasketItemService basketItemService, BasketItemRepository basketItemRepository) {
        this.basketItemService = basketItemService;
        this.basketItemRepository = basketItemRepository;
    }

    /**
     * {@code POST  /basket-items} : Create a new basketItem.
     *
     * @param basketItem the basketItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new basketItem, or with status {@code 400 (Bad Request)} if the basketItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/basket-items")
    public Mono<ResponseEntity<BasketItem>> createBasketItem(@Valid @RequestBody BasketItem basketItem) throws URISyntaxException {
        log.debug("REST request to save BasketItem : {}", basketItem);
        if (basketItem.getId() != null) {
            throw new BadRequestAlertException("A new basketItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return basketItemService
            .save(basketItem)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/basket-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /basket-items/:id} : Updates an existing basketItem.
     *
     * @param id the id of the basketItem to save.
     * @param basketItem the basketItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketItem,
     * or with status {@code 400 (Bad Request)} if the basketItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the basketItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/basket-items/{id}")
    public Mono<ResponseEntity<BasketItem>> updateBasketItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BasketItem basketItem
    ) throws URISyntaxException {
        log.debug("REST request to update BasketItem : {}, {}", id, basketItem);
        if (basketItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return basketItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return basketItemService
                    .save(basketItem)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /basket-items/:id} : Partial updates given fields of an existing basketItem, field will ignore if it is null
     *
     * @param id the id of the basketItem to save.
     * @param basketItem the basketItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketItem,
     * or with status {@code 400 (Bad Request)} if the basketItem is not valid,
     * or with status {@code 404 (Not Found)} if the basketItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the basketItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/basket-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BasketItem>> partialUpdateBasketItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BasketItem basketItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update BasketItem partially : {}, {}", id, basketItem);
        if (basketItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return basketItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BasketItem> result = basketItemService.partialUpdate(basketItem);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /basket-items} : get all the basketItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of basketItems in body.
     */
    @GetMapping("/basket-items")
    public Mono<ResponseEntity<List<BasketItem>>> getAllBasketItems(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of BasketItems");
        return basketItemService
            .countAll()
            .zipWith(basketItemService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /basket-items/:id} : get the "id" basketItem.
     *
     * @param id the id of the basketItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the basketItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/basket-items/{id}")
    public Mono<ResponseEntity<BasketItem>> getBasketItem(@PathVariable Long id) {
        log.debug("REST request to get BasketItem : {}", id);
        Mono<BasketItem> basketItem = basketItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(basketItem);
    }

    /**
     * {@code DELETE  /basket-items/:id} : delete the "id" basketItem.
     *
     * @param id the id of the basketItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/basket-items/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBasketItem(@PathVariable Long id) {
        log.debug("REST request to delete BasketItem : {}", id);
        return basketItemService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
