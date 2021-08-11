package com.testcasemng.tool.utils;

import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GitUtils {
    private Repository repository;
    private File currentFile;
    private String createBy;
    private Date createdDate;
    private String reviewedBy;
    private Date reviewedDate;
    private String testedBy;
    private Date testedDate;
    private String latestLog;
    private String latestVersion;

    public GitUtils(File file) throws IOException {
        this.setCreateBy("");
        this.setReviewedBy("");
        this.setTestedBy("");
        this.setLatestLog("");
        this.setLatestVersion("");
        this.setCurrentFile(file);
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.findGitDir(file);
        this.setRepository(repositoryBuilder.build());
    }

    public boolean isInGitRepository() {
        return (repository.getDirectory() != null);
    }

    public void getChildOfMerge(RevCommit parent) throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .add(repository.resolve(currentBranch))
                    .call();

            for (RevCommit rev : logs) {
                RevCommit[] revs = rev.getParents();
                if(revs!= null && ArrayUtils.contains(revs, parent)) {
                    PersonIdent authorIdent = rev.getAuthorIdent();
                    this.setReviewedBy(String.format("%s<%s>", authorIdent.getName(), authorIdent.getEmailAddress()));
                    this.setReviewedDate(authorIdent.getWhen());
                }
            }
        }

    }

    public void parseLatestCommit() throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .add(repository.resolve(currentBranch))
                    .setMaxCount(1)
                    .call();

            RevCommit lastCommit = logs.iterator().next();
            if (lastCommit != null) {
                this.setLatestLog(lastCommit.getFullMessage());
                this.setLatestVersion(lastCommit.getName());
            }
        }
    }

    public void parseGit() throws IOException, GitAPIException {
        String ret = "";
        int i = 0;
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .add(repository.resolve(currentBranch))
                    .addPath(currentFile.getName())
                    .call();

            RevCommit firstCommit = null;
            RevCommit parent = null;
            RevCommit latestTest = null;
            RevCommit latestReview = null;
            for (RevCommit rev : logs) {
                if (firstCommit == null)
                    firstCommit = rev;
                if (rev.getCommitTime() < firstCommit.getCommitTime())
                    firstCommit = rev;

                TestCaseTemplate currentTemplate = loadFileContent(rev);
                PersonIdent indent = rev.getAuthorIdent();
                TestCaseTemplate parentTemplate = null;
                if (rev.getParentCount() > 0)
                    parentTemplate = loadFileContent(rev.getParents()[0]);
                if (!isDesignChange(currentTemplate, parentTemplate)) {
                    if (latestTest == null)
                        latestTest = rev;
                    if (rev.getCommitTime() > latestTest.getCommitTime())
                        latestTest = rev;
                } else {
                    if (latestReview == null)
                        latestReview = rev;
                    if (rev.getCommitTime() > latestReview.getCommitTime())
                        latestReview = rev;
                }

            }
            if (firstCommit != null) {
                PersonIdent authorIdent = firstCommit.getAuthorIdent();
                this.setCreateBy(String.format("%s<%s>", authorIdent.getName(), authorIdent.getEmailAddress()));
                this.setCreatedDate(authorIdent.getWhen());
            }
            if (latestTest != null) {
                PersonIdent authorIdent = latestTest.getAuthorIdent();
                this.setTestedBy(String.format("%s<%s>", authorIdent.getName(), authorIdent.getEmailAddress()));
            }

            if (latestReview != null) {
                getChildOfMerge(latestReview);
            }
        }
    }

    public static boolean isDesignChange(TestCaseTemplate current, TestCaseTemplate previous) {
        if (previous == null)
            return true;
        if (previous != null && current != null) {
            if (!current.getTestcaseID().equals(previous.getTestcaseID()))
                return true;
            if (!current.getTestcaseName().equals(previous.getTestcaseName()))
                return true;
            if (!current.getTestcaseName().equals(previous.getTestcaseName()))
                return true;
            if (!current.getTestcaseDesc().equals(previous.getTestcaseDesc()))
                return true;
            if (!current.getPreCondition().equals(previous.getPreCondition()))
                return true;
            if (!current.getPostCondition().equals(previous.getPostCondition()))
                return true;
            if (current.getTestSteps() == null && previous.getTestSteps() != null)
                return true;
            else if (current.getTestSteps() != null && previous.getTestSteps() == null)
                return true;
            else if (current.getTestSteps() != null && previous.getTestSteps() != null) {
                List<TestStep> currentSteps = current.getTestSteps();
                List<TestStep> previousSteps = previous.getTestSteps();
                if (currentSteps.size() != previousSteps.size())
                    return true;
                else {
                    for (int i = 0; i < currentSteps.size(); i++) {
                        if (!currentSteps.get(i).getDetails().equals(previousSteps.get(i).getDetails()))
                            return true;
                        if (!currentSteps.get(i).getData().equals(previousSteps.get(i).getData()))
                            return true;
                        if (!currentSteps.get(i).getExpectedResults().equals(previousSteps.get(i).getExpectedResults()))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public TestCaseTemplate loadFileContent(RevCommit rev) throws IOException, GitAPIException {
        TestCaseTemplate template = null;
        // now try to find a specific file
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(rev.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(currentFile.getName()));
            if (!treeWalk.next()) {
                return null;
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            template = MarkdownTestCaseTemplate.readFromStream(loader.openStream());
        }
        return template;
    }

    /*public String getTesterPerson() throws IOException, GitAPIException {
        String ret = "";
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .setMaxCount(1)
                    .add(repository.resolve(currentBranch))
                    .addPath(currentFile.getName())
                    .call();

            RevCommit lastCommit = logs.iterator().next();
            if (lastCommit != null) {
                PersonIdent authorIdent = lastCommit.getAuthorIdent();
                ret = String.format("%s<%s>", authorIdent.getName(), authorIdent.getEmailAddress());
            }
        }
        return ret;
    }*/

    /*public Date getTestDate() throws IOException, GitAPIException {
        Date ret = null;
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .setMaxCount(1)
                    .add(repository.resolve(currentBranch))
                    .addPath(currentFile.getName())
                    .call();

            RevCommit lastCommit = logs.iterator().next();
            if (lastCommit != null) {
                PersonIdent authorIdent = lastCommit.getAuthorIdent();
                ret = authorIdent.getWhen();
            }
        }
        return ret;
    }*/

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Date getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Date reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getTestedBy() {
        return testedBy;
    }

    public void setTestedBy(String testedBy) {
        this.testedBy = testedBy;
    }

    public Date getTestedDate() {
        return testedDate;
    }

    public void setTestedDate(Date testedDate) {
        this.testedDate = testedDate;
    }

    public String getLatestLog() {
        return latestLog;
    }

    public void setLatestLog(String latestLog) {
        this.latestLog = latestLog;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
}
