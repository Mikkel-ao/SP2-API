package app.populators;

import jakarta.persistence.EntityManagerFactory;

public class DataPopulator {
    private final UserRolePopulator userRolePopulator;
    private final PostPopulator postPopulator;
    private final CommentPopulator commentPopulator;
    private final VotePopulator votePopulator;

    public DataPopulator(EntityManagerFactory emf) {
        this.userRolePopulator = new UserRolePopulator(emf);
        this.postPopulator = new PostPopulator(emf);
        this.commentPopulator = new CommentPopulator(emf);
        this.votePopulator = new VotePopulator(emf);
    }

    public void populateAll() {
        userRolePopulator.populate();
        postPopulator.populate();
        commentPopulator.populate();
        votePopulator.populate();
    }
}
