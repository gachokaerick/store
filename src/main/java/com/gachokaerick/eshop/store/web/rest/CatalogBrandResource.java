package com.gachokaerick.eshop.store.web.rest;

import com.gachokaerick.eshop.store.domain.CatalogBrand;
import com.gachokaerick.eshop.store.repository.CatalogBrandRepository;
import com.gachokaerick.eshop.store.service.CatalogBrandService;
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
 * REST controller for managing {@link com.gachokaerick.eshop.store.domain.CatalogBrand}.
 */
@RestController
@RequestMapping("/api")
public class CatalogBrandResource {

    private final Logger log = LoggerFactory.getLogger(CatalogBrandResource.class);

    private static final String ENTITY_NAME = "catalogBrand";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CatalogBrandService catalogBrandService;

    private final CatalogBrandRepository catalogBrandRepository;

    public CatalogBrandResource(CatalogBrandService catalogBrandService, CatalogBrandRepository catalogBrandRepository) {
        this.catalogBrandService = catalogBrandService;
        this.catalogBrandRepository = catalogBrandRepository;
    }

    /**
     * {@code POST  /catalog-brands} : Create a new catalogBrand.
     *
     * @param catalogBrand the catalogBrand to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogBrand, or with status {@code 400 (Bad Request)} if the catalogBrand has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/catalog-brands")
    public Mono<ResponseEntity<CatalogBrand>> createCatalogBrand(@Valid @RequestBody CatalogBrand catalogBrand) throws URISyntaxException {
        log.debug("REST request to save CatalogBrand : {}", catalogBrand);
        if (catalogBrand.getId() != null) {
            throw new BadRequestAlertException("A new catalogBrand cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return catalogBrandService
            .save(catalogBrand)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/catalog-brands/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /catalog-brands/:id} : Updates an existing catalogBrand.
     *
     * @param id the id of the catalogBrand to save.
     * @param catalogBrand the catalogBrand to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogBrand,
     * or with status {@code 400 (Bad Request)} if the catalogBrand is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogBrand couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/catalog-brands/{id}")
    public Mono<ResponseEntity<CatalogBrand>> updateCatalogBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CatalogBrand catalogBrand
    ) throws URISyntaxException {
        log.debug("REST request to update CatalogBrand : {}, {}", id, catalogBrand);
        if (catalogBrand.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogBrand.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogBrandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return catalogBrandService
                    .save(catalogBrand)
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
     * {@code PATCH  /catalog-brands/:id} : Partial updates given fields of an existing catalogBrand, field will ignore if it is null
     *
     * @param id the id of the catalogBrand to save.
     * @param catalogBrand the catalogBrand to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogBrand,
     * or with status {@code 400 (Bad Request)} if the catalogBrand is not valid,
     * or with status {@code 404 (Not Found)} if the catalogBrand is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogBrand couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/catalog-brands/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CatalogBrand>> partialUpdateCatalogBrand(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CatalogBrand catalogBrand
    ) throws URISyntaxException {
        log.debug("REST request to partial update CatalogBrand partially : {}, {}", id, catalogBrand);
        if (catalogBrand.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogBrand.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return catalogBrandRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CatalogBrand> result = catalogBrandService.partialUpdate(catalogBrand);

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
     * {@code GET  /catalog-brands} : get all the catalogBrands.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of catalogBrands in body.
     */
    @GetMapping("/catalog-brands")
    public Mono<ResponseEntity<List<CatalogBrand>>> getAllCatalogBrands(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of CatalogBrands");
        return catalogBrandService
            .countAll()
            .zipWith(catalogBrandService.findAll(pageable).collectList())
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
     * {@code GET  /catalog-brands/:id} : get the "id" catalogBrand.
     *
     * @param id the id of the catalogBrand to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogBrand, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/catalog-brands/{id}")
    public Mono<ResponseEntity<CatalogBrand>> getCatalogBrand(@PathVariable Long id) {
        log.debug("REST request to get CatalogBrand : {}", id);
        Mono<CatalogBrand> catalogBrand = catalogBrandService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogBrand);
    }

    /**
     * {@code DELETE  /catalog-brands/:id} : delete the "id" catalogBrand.
     *
     * @param id the id of the catalogBrand to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/catalog-brands/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCatalogBrand(@PathVariable Long id) {
        log.debug("REST request to delete CatalogBrand : {}", id);
        return catalogBrandService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
