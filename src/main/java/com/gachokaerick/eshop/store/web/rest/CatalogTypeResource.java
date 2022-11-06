package com.gachokaerick.eshop.store.web.rest;

import com.gachokaerick.eshop.store.domain.CatalogType;
import com.gachokaerick.eshop.store.repository.CatalogTypeRepository;
import com.gachokaerick.eshop.store.service.CatalogTypeService;
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
 * REST controller for managing {@link com.gachokaerick.eshop.store.domain.CatalogType}.
 */
@RestController
@RequestMapping("/api")
public class CatalogTypeResource {

    private final Logger log = LoggerFactory.getLogger(CatalogTypeResource.class);

    private static final String ENTITY_NAME = "catalogType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CatalogTypeService catalogTypeService;

    private final CatalogTypeRepository catalogTypeRepository;

    public CatalogTypeResource(CatalogTypeService catalogTypeService, CatalogTypeRepository catalogTypeRepository) {
        this.catalogTypeService = catalogTypeService;
        this.catalogTypeRepository = catalogTypeRepository;
    }

    /**
     * {@code POST  /catalog-types} : Create a new catalogType.
     *
     * @param catalogType the catalogType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogType, or with status {@code 400 (Bad Request)} if the catalogType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/catalog-types")
    public Mono<ResponseEntity<CatalogType>> createCatalogType(@Valid @RequestBody CatalogType catalogType) throws URISyntaxException {
        log.debug("REST request to save CatalogType : {}", catalogType);
        if (catalogType.getId() != null) {
            throw new BadRequestAlertException("A new catalogType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return catalogTypeService
            .save(catalogType)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/catalog-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /catalog-types/:id} : Updates an existing catalogType.
     *
     * @param id the id of the catalogType to save.
     * @param catalogType the catalogType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogType,
     * or with status {@code 400 (Bad Request)} if the catalogType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/catalog-types/{id}")
    public Mono<ResponseEntity<CatalogType>> updateCatalogType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CatalogType catalogType
    ) throws URISyntaxException {
        log.debug("REST request to update CatalogType : {}, {}", id, catalogType);
        if (catalogType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return catalogTypeService
                    .save(catalogType)
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
     * {@code PATCH  /catalog-types/:id} : Partial updates given fields of an existing catalogType, field will ignore if it is null
     *
     * @param id the id of the catalogType to save.
     * @param catalogType the catalogType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogType,
     * or with status {@code 400 (Bad Request)} if the catalogType is not valid,
     * or with status {@code 404 (Not Found)} if the catalogType is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/catalog-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CatalogType>> partialUpdateCatalogType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CatalogType catalogType
    ) throws URISyntaxException {
        log.debug("REST request to partial update CatalogType partially : {}, {}", id, catalogType);
        if (catalogType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CatalogType> result = catalogTypeService.partialUpdate(catalogType);

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
     * {@code GET  /catalog-types} : get all the catalogTypes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of catalogTypes in body.
     */
    @GetMapping("/catalog-types")
    public Mono<ResponseEntity<List<CatalogType>>> getAllCatalogTypes(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of CatalogTypes");
        return catalogTypeService
            .countAll()
            .zipWith(catalogTypeService.findAll(pageable).collectList())
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
     * {@code GET  /catalog-types/:id} : get the "id" catalogType.
     *
     * @param id the id of the catalogType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/catalog-types/{id}")
    public Mono<ResponseEntity<CatalogType>> getCatalogType(@PathVariable Long id) {
        log.debug("REST request to get CatalogType : {}", id);
        Mono<CatalogType> catalogType = catalogTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogType);
    }

    /**
     * {@code DELETE  /catalog-types/:id} : delete the "id" catalogType.
     *
     * @param id the id of the catalogType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/catalog-types/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCatalogType(@PathVariable Long id) {
        log.debug("REST request to delete CatalogType : {}", id);
        return catalogTypeService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
