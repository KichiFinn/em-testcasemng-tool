package com.testcasemng.tool.utils;

import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitUtils {
    private final Repository repository;
    private final File currentFile;
    private String createBy;
    private Date createdDate;
    private String reviewedBy;
    private Date reviewedDate;
    private String testedBy;
    private String latestLog;
    private String latestVersion;

    public GitUtils(File file) throws IOException {
        this.setCreateBy("");
        this.setReviewedBy("");
        this.setTestedBy("");
        this.setLatestLog("");
        this.setLatestVersion("");
        this.currentFile = file;
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.findGitDir(file);
        this.repository = repositoryBuilder.build();
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
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .add(repository.resolve(currentBranch))
                    .addPath(FileUtils.getRelativePathToGit(currentFile, repository.getDirectory()))
                    .call();

            RevCommit firstCommit = null;
            RevCommit latestTest = null;
            RevCommit latestReview = null;
            for (RevCommit rev : logs) {
                if (firstCommit == null)
                    firstCommit = rev;
                if (rev.getCommitTime() < firstCommit.getCommitTime())
                    firstCommit = rev;

                TestCaseTemplate currentTemplate = loadFileContent(rev);
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
                PersonIdent authorIndent = firstCommit.getAuthorIdent();
                this.setCreateBy(String.format("%s<%s>", authorIndent.getName(), authorIndent.getEmailAddress()));
                this.setCreatedDate(authorIndent.getWhen());
            }
            if (latestTest != null) {
                PersonIdent authorIndent = latestTest.getAuthorIdent();
                this.setTestedBy(String.format("%s<%s>", authorIndent.getName(), authorIndent.getEmailAddress()));
            }

            if (latestReview != null) {
                PersonIdent authorIndent = latestReview.getAuthorIdent();
                this.setReviewedBy(String.format("%s<%s>", authorIndent.getName(), authorIndent.getEmailAddress()));
                this.setReviewedDate(authorIndent.getWhen());
            }
        }
    }

    public static boolean isDesignChange(TestCaseTemplate current, TestCaseTemplate previous) {
        if (previous == null)
            return true;
        if (current != null) {
            if (!current.getTestcaseID().equals(previous.getTestcaseID()))
                return true;
            if (!current.getTestcaseName().equals(previous.getTestcaseName()))
                return true;
            if (!current.getTestScriptLink().equals(previous.getTestScriptLink()))
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

    public static boolean isTestResultChange(TestCaseTemplate current, TestCaseTemplate previous) {
        if (previous == null && current != null) {
            if (!current.getTestResults().equalsIgnoreCase(Constants.TEST_FIELD_NO_DATA))
                return true;
        } else if (previous != null && current != null) {
            if (!current.getTestDate().equals(previous.getTestDate()))
                return true;
            if (!current.getTestResults().equals(previous.getTestResults()))
                return true;
        }
        return false;
    }

    public TestCaseTemplate loadFileContent(RevCommit rev) throws IOException {
        TestCaseTemplate template;
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(rev.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(FileUtils.getRelativePathToGit(currentFile, repository.getDirectory())));
            if (!treeWalk.next()) {
                return null;
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            template = MarkdownTestCaseTemplate.readFromStream(loader.openStream());
        }
        return template;
    }


    public AnalysisTemplate parseHistoricalResults() throws IOException, GitAPIException {
        AnalysisTemplate template = new AnalysisTemplate();
        List<ShortTestResult> tests = new ArrayList<>();
        try (Git git = new Git(repository)) {
            String currentBranch = repository.getBranch();
            Iterable<RevCommit> logs = git.log()
                    .add(repository.resolve(currentBranch))
                    .addPath(FileUtils.getRelativePathToGit(currentFile, repository.getDirectory()))
                    .call();

            for (RevCommit rev : logs) {
                TestCaseTemplate currentTemplate = loadFileContent(rev);
                TestCaseTemplate parentTemplate = null;
                if (rev.getParentCount() > 0)
                    parentTemplate = loadFileContent(rev.getParents()[0]);
                if (isTestResultChange(currentTemplate, parentTemplate)) {
                    ShortTestResult result = new ShortTestResult();
                    result.setResult(currentTemplate.getTestResults());
                    result.setDateTest(currentTemplate.getTestDate());
                    result.setId(currentTemplate.getTestcaseID());
                    result.setName(currentTemplate.getTestcaseName());
                    tests.add(result);
                    switch (currentTemplate.getTestResults()) {
                        case Constants.TEST_RESULT_PASS:
                            template.setPass(template.getPass() + 1);
                            break;
                        case Constants.TEST_RESULT_FAIL:
                            template.setFail(template.getFail() + 1);
                            break;
                        case Constants.TEST_RESULT_NOT_EXECUTED:
                            template.setNotExecuted(template.getNotExecuted() + 1);
                            break;
                        case Constants.TEST_RESULT_SUSPENDED:
                            template.setSuspend(template.getSuspend() + 1);
                            break;
                        default:
                            template.setOthers(template.getOthers());
                            break;
                    }
                }
            }
        }
        template.setTests(tests);
        return template;
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
