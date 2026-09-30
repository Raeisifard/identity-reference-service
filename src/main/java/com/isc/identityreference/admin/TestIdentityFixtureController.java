package com.isc.identityreference.admin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;import org.springframework.http.*;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import org.springframework.web.multipart.MultipartFile;import java.util.*;
@RestController @RequestMapping("/api/v1/admin/test-data/identities") @ConditionalOnProperty(prefix="identity-reference.admin-console.sections",name="test-data",havingValue="true")
public class TestIdentityFixtureController{
 private final TestIdentityFixtureService service;public TestIdentityFixtureController(TestIdentityFixtureService s){service=s;}
 @GetMapping public List<TestIdentityFixtureService.TestIdentityView> list(){return service.list();}
 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> create(@RequestPart("data")TestIdentityFixtureService.TestIdentityRequest data,@RequestPart(value="photo",required=false)MultipartFile photo,Authentication a)throws Exception{return ResponseEntity.status(HttpStatus.CREATED).body(service.create(data,photo,actor(a)));}
 @PutMapping("/{id}") public TestIdentityFixtureService.TestIdentityView update(@PathVariable UUID id,@RequestBody TestIdentityFixtureService.TestIdentityRequest r,Authentication a){return service.update(id,r,actor(a));}
 @PostMapping("/{id}/photo") public TestIdentityFixtureService.TestIdentityView photo(@PathVariable UUID id,@RequestPart("photo")MultipartFile p,Authentication a)throws Exception{return service.replacePhoto(id,p,actor(a));}
 @PostMapping("/{id}/biometric/rebuild") public TestIdentityFixtureService.TestIdentityView rebuild(@PathVariable UUID id,Authentication a){return service.rebuildBiometric(id,actor(a));}
 @GetMapping("/{id}/photo") public ResponseEntity<byte[]> photo(@PathVariable UUID id){var p=service.photo(id);return ResponseEntity.ok().contentType(MediaType.parseMediaType(p.contentType())).cacheControl(CacheControl.noStore()).body(p.bytes());}
 @PostMapping("/{id}/retire") public TestIdentityFixtureService.TestIdentityView retire(@PathVariable UUID id,@RequestParam(required=false,defaultValue="TEST_FIXTURE_RETIRE")String reason,Authentication a){return service.retire(id,reason,actor(a));}
 private static String actor(Authentication a){return a==null?"DEV_CONSOLE":a.getName();}
 @ExceptionHandler(IllegalArgumentException.class)ResponseEntity<Map<String,String>> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error","INVALID_REQUEST","message",e.getMessage()));}
 @ExceptionHandler(IllegalStateException.class)ResponseEntity<Map<String,String>> conflict(IllegalStateException e){return ResponseEntity.status(409).body(Map.of("error","CONFLICT","message",e.getMessage()));}
 @ExceptionHandler(NoSuchElementException.class)ResponseEntity<Map<String,String>> nf(NoSuchElementException e){return ResponseEntity.status(404).body(Map.of("error","NOT_FOUND","message",e.getMessage()));}
}
