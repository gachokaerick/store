package com.gachokaerick.eshop.store.web.rest;

import com.gachokaerick.eshop.store.domain.Buyer;
import com.gachokaerick.eshop.store.repository.BuyerRepository;
import com.gachokaerick.eshop.store.service.BuyerService;
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
 * REST controller for managing {@link com.gachokaerick.eshop.store.domain.Buyer}.
 */
@RestController
@RequestMapping("/api")
public class BuyerResource {

    private final Logger log = LoggerFactory.getLogger(BuyerResource.class);

    private static final String ENTITY_NAME = "buyer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuyerService buyerService;

    private final BuyerRepository buyerRepository;

    public BuyerResource(BuyerService buyerService, BuyerRepository buyerRepository) {
        this.buyerService = buyerService;
        this.buyerRepository = buyerRepository;
    }

    /**
     * {@code POST  /buyers} : Create a new buyer.
     *
     * @param buyer the buyer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new buyer, or with status {@code 400 (Bad Request)} if the buyer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/buyers")
    public Mono<ResponseEntity<Buyer>> createBuyer(@Valid @RequestBody Buyer buyer) throws URISyntaxException {
        log.debug("REST request to save Buyer : {}", buyer);
        if (buyer.getId() != null) {
            throw new BadRequestAlertException("A new buyer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return buyerService
            .save(buyer)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/buyers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /buyers/:id} : Updates an existing buyer.
     *
     * @param id the id of the buyer to save.
     * @param buyer the buyer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buyer,
     * or with status {@code 400 (Bad Request)} if the buyer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the buyer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/buyers/{id}")
    public Mono<ResponseEntity<Buyer>> updateBuyer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Buyer buyer
    ) throws URISyntaxException {
        log.debug("REST request to update Buyer : {}, {}", id, buyer);
        if (buyer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buyer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return buyerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return buyerService
                    .save(buyer)
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
     * {@code PATCH  /buyers/:id} : Partial updates given fields of an existing buyer, field will ignore if it is null
     *
     * @param id the id of the buyer to save.
     * @param buyer the buyer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated buyer,
     * or with status {@code 400 (Bad Request)} if the buyer is not valid,
     * or with status {@code 404 (Not Found)} if the buyer is not found,
     * or with status {@code 500 (Internal Server Error)} if the buyer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/buyers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Buyer>> partialUpdateBuyer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Buyer buyer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Buyer partially : {}, {}", id, buyer);
        if (buyer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, buyer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return buyerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Buyer> result = buyerService.partialUpdate(buyer);

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
     * {@code GET  /buyers} : get all the buyers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buyers in body.
     */
    @GetMapping("/buyers")
    public Mono<ResponseEntity<List<Buyer>>> getAllBuyers(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Buyers");
        return buyerService
            .countAll()
            .zipWith(buyerService.findAll(pageable).collectList())
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
     * {@code GET  /buyers/:id} : get the "id" buyer.
     *
     * @param id the id of the buyer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the buyer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/buyers/{id}")
    public Mono<ResponseEntity<Buyer>> getBuyer(@PathVariable Long id) {
        log.debug("REST request to get Buyer : {}", id);
        Mono<Buyer> buyer = buyerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(buyer);
    }

    /**
     * {@code DELETE  /buyers/:id} : delete the "id" buyer.
     *
     * @param id the id of the buyer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/buyers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBuyer(@PathVariable Long id) {
        log.debug("REST request to delete Buyer : {}", id);
        return buyerService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
