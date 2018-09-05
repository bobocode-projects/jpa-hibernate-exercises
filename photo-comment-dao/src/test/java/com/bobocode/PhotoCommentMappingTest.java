package com.bobocode;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.EntityManagerUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.List;

import static com.bobocode.util.PhotoTestDataGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class PhotoCommentMappingTest {
    private static EntityManagerUtil emUtil;

    @BeforeAll
    public static void setup() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PhotoComments");
        emUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @Test
    public void testSavePhotoOnly() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        assertThat(photo.getId(), notNullValue());
    }

    @Test
    public void testSavePhotoCommentOnly() {
        PhotoComment photoComment = createRandomPhotoComment();
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(photoComment));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(PersistenceException.class));
        }
    }

    @Test
    public void testAddNewComment() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        PhotoComment photoComment = createRandomPhotoComment();
        photoComment.setPhoto(photo);
        emUtil.performWithinTx(entityManager -> entityManager.persist(photoComment));

        assertThat(photoComment.getId(), notNullValue());
        emUtil.performWithinTx(entityManager -> {
            PhotoComment managedPhotoComment = entityManager.find(PhotoComment.class, photoComment.getId());
            assertThat(managedPhotoComment.getPhoto(), equalTo(photo));
        });
        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments(), hasItem(photoComment));
        });
    }

    @Test
    public void testAddComments() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        List<PhotoComment> listOfComments = createListOfRandomComments(5);
        listOfComments.forEach(comment -> comment.setPhoto(photo));

        emUtil.performWithinTx(entityManager -> listOfComments.forEach(entityManager::persist));

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments(), containsInAnyOrder(listOfComments.toArray()));
        });
    }

}
