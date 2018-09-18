package com.bobocode;

import com.bobocode.model.Photo;
import com.bobocode.model.PhotoComment;
import com.bobocode.util.EntityManagerUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.bobocode.util.PhotoTestDataGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class PhotoCommentMappingTest {
    private static EntityManagerUtil emUtil;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("PhotoComments");
        emUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @AfterAll
    static void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void testCommentsListIsInitialized() {
        Photo photo = new Photo();
        List<PhotoComment> comments = photo.getComments();

        assertThat(comments, notNullValue());
    }

    @Test
    public void testCommentsSetterIsPrivate() throws NoSuchMethodException {
        Method setComments = Photo.class.getDeclaredMethod("setComments", List.class);

        assertThat(setComments.getModifiers(), equalTo(Modifier.PRIVATE));
    }

    @Test
    public void testPhotoTableNameIsSpecified() {
        Table table = Photo.class.getAnnotation(Table.class);
        String tableName = table.name();

        assertThat(tableName, equalTo("photo"));
    }

    @Test
    public void testPhotoCommentTableNameIsSpecified() {
        Table table = PhotoComment.class.getAnnotation(Table.class);

        assertThat(table.name(), equalTo("photo_comment"));
    }

    @Test
    public void testPhotoUrlIsNotNullAndUnique() throws NoSuchFieldException {
        Field url = Photo.class.getDeclaredField("url");
        Column column = url.getAnnotation(Column.class);

        assertThat(column.nullable(), is(false));
        assertThat(column.unique(), is(true));
    }

    @Test
    public void testPhotoCommentTextIsMandatory() throws NoSuchFieldException {
        Field text = PhotoComment.class.getDeclaredField("text");
        Column column = text.getAnnotation(Column.class);

        assertThat(column.nullable(), is(false));
    }

    @Test
    public void testCascadeTypeAllIsEnabledForComments() throws NoSuchFieldException {
        Field comments = Photo.class.getDeclaredField("comments");
        OneToMany oneToMany = comments.getAnnotation(OneToMany.class);
        CascadeType[] expectedCascade = {CascadeType.ALL};

        assertThat(oneToMany.cascade(), equalTo(expectedCascade));
    }

    @Test
    public void testOrphanRemovalIsEnabledForComments() throws NoSuchFieldException {
        Field comments = Photo.class.getDeclaredField("comments");
        OneToMany oneToMany = comments.getAnnotation(OneToMany.class);

        assertThat(oneToMany.orphanRemoval(), is(true));
    }

    @Test
    public void testForeignKeyColumnIsSpecified() throws NoSuchFieldException {
        Field photo = PhotoComment.class.getDeclaredField("photo");
        JoinColumn joinColumn = photo.getAnnotation(JoinColumn.class);

        assertThat(joinColumn.name(), equalTo("photo_id"));
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
    public void testCommentCannotExistsWithoutPhoto() throws NoSuchFieldException {
        Field photo = PhotoComment.class.getDeclaredField("photo");
        ManyToOne manyToOne = photo.getAnnotation(ManyToOne.class);

        assertThat(manyToOne.optional(), is(false));
    }


    @Test
    public void testSaveNewComment() {
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
    public void testAddNewComment() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        PhotoComment photoComment = createRandomPhotoComment();
        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            managedPhoto.addComment(photoComment);
        });

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
    public void testSaveNewComments() {
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

    @Test
    public void testAddNewComments() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));
        List<PhotoComment> listOfComments = createListOfRandomComments(5);

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            listOfComments.forEach(managedPhoto::addComment);
        });

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments(), containsInAnyOrder(listOfComments.toArray()));
        });
    }

    @Test
    public void testRemoveComment() {
        Photo photo = createRandomPhoto();
        PhotoComment photoComment = createRandomPhotoComment();
        List<PhotoComment> commentList = createListOfRandomComments(5);
        photo.addComment(photoComment);
        commentList.forEach(photo::addComment);
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            PhotoComment managedComment = entityManager.find(PhotoComment.class, photoComment.getId());
            managedPhoto.removeComment(managedComment);
        });


        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            PhotoComment managedPhotoComment = entityManager.find(PhotoComment.class, photoComment.getId());

            assertThat(managedPhoto.getComments(), not(hasItem(photoComment)));
            assertThat(managedPhoto.getComments(), containsInAnyOrder(commentList.toArray()));
            assertThat(managedPhotoComment, nullValue());
        });

    }

}
