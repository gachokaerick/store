package com.gachokaerick.eshop.store.web.rest;

import com.gachokaerick.eshop.store.domain.CatalogItem;
import com.gachokaerick.eshop.store.repository.CatalogItemRepository;
import com.gachokaerick.eshop.store.service.CatalogItemService;
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
 * REST controller for managing {@link com.gachokaerick.eshop.store.domain.CatalogItem}.
 */
@RestController
@RequestMapping("/api")
public class CatalogItemResource {

    private final Logger log = LoggerFactory.getLogger(CatalogItemResource.class);

    private static final String ENTITY_NAME = "catalogItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CatalogItemService catalogItemService;

    private final CatalogItemRepository catalogItemRepository;

    public CatalogItemResource(CatalogItemService catalogItemService, CatalogItemRepository catalogItemRepository) {
        this.catalogItemService = catalogItemService;
        this.catalogItemRepository = catalogItemRepository;
    }

    /**
     * {@code POST  /catalog-items} : Create a new catalogItem.
     *
     * @param catalogItem the catalogItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogItem, or with status {@code 400 (Bad Request)} if the catalogItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/catalog-items")
    public Mono<ResponseEntity<CatalogItem>> createCatalogItem(@Valid @RequestBody CatalogItem catalogItem) throws URISyntaxException {
        log.debug("REST request to save CatalogItem : {}", catalogItem);
        if (catalogItem.getId() != null) {
            throw new BadRequestAlertException("A new catalogItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return catalogItemService
            .save(catalogItem)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/catalog-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /catalog-items/:id} : Updates an existing catalogItem.
     *
     * @param id the id of the catalogItem to save.
     * @param catalogItem the catalogItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogItem,
     * or with status {@code 400 (Bad Request)} if the catalogItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/catalog-items/{id}")
    public Mono<ResponseEntity<CatalogItem>> updateCatalogItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CatalogItem catalogItem
    ) throws URISyntaxException {
        log.debug("REST request to update CatalogItem : {}, {}", id, catalogItem);
        if (catalogItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return catalogItemService
                    .save(catalogItem)
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
     * {@code PATCH  /catalog-items/:id} : Partial updates given fields of an existing catalogItem, field will ignore if it is null
     *
     * @param id the id of the catalogItem to save.
     * @param catalogItem the catalogItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogItem,
     * or with status {@code 400 (Bad Request)} if the catalogItem is not valid,
     * or with status {@code 404 (Not Found)} if the catalogItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/catalog-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CatalogItem>> partialUpdateCatalogItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CatalogItem catalogItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update CatalogItem partially : {}, {}", id, catalogItem);
        if (catalogItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CatalogItem> result = catalogItemService.partialUpdate(catalogItem);

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
     * {@code GET  /catalog-items} : get all the catalogItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of catalogItems in body.
     */
    @GetMapping("/catalog-items")
    public Mono<ResponseEntity<List<CatalogItem>>> getAllCatalogItems(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of CatalogItems");
        return catalogItemService
            .countAll()
            .zipWith(catalogItemService.findAll(pageable).collectList())
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
     * {@code GET  /catalog-items/:id} : get the "id" catalogItem.
     *
     * @param id the id of the catalogItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/catalog-items/{id}")
    public Mono<ResponseEntity<CatalogItem>> getCatalogItem(@PathVariable Long id) {
        log.debug("REST request to get CatalogItem : {}", id);
        Mono<CatalogItem> catalogItem = catalogItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogItem);
    }

    /**
     * {@code DELETE  /catalog-items/:id} : delete the "id" catalogItem.
     *
     * @param id the id of the catalogItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/catalog-items/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCatalogItem(@PathVariable Long id) {
        log.debug("REST request to delete CatalogItem : {}", id);
        return catalogItemService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
