<?php
require_once('init.php');

// Task ID needs to be the newest ID available
$taskID             = generateNewID($pdo);
$taskName           = $_SESSION['add_taskName'];
$startDate          = $_SESSION['add_startDate'];
$endDate            = $_SESSION['add_endDate'];
$status             = $_SESSION['add_taskStatus'];
$taskAllocated      = $_SESSION['add_taskAllocated'];
$numberOfElements   = $_SESSION['add_numberOfElements'];

// Move post into session and sanitise
for ($i = 1; $i > $numberOfElements; $i++)
{
    $_SESSION['taskDesc_'.$i] = filter_var($_POST['taskDesc_'.$i],FILTER_SANITIZE_STRING);
    $_SESSION['taskComment_'.$i] = filter_var($_POST['taskComment_'.$i],FILTER_SANITIZE_STRING);

}

// Enter into Task table of database
try
{
    $stmt = $pdo->prepare("INSERT INTO tbl_tasks (TaskID, TaskName,StartDate,EndDate,Status,TaskOwner) VALUES (:taskID, :taskName, :startDate, :endDate, :status, :taskOwner)");
    // Bind parameters and run

    $stmt->bindParam(':taskID',    $taskID);
    $stmt->bindParam(':taskName',  $taskName);
    $stmt->bindParam(':startDate', $startDate);
    $stmt->bindParam(':endDate',   $endDate);
    $stmt->bindParam(':status',    $status);
    $stmt->bindParam(':taskOwner', $taskAllocated);

    var_dump($taskID, $taskName, $startDate, $endDate, $status, $taskAllocated);
    $stmt->execute();
}
catch (PDOException $ex)
{
    errorHandler($ex->getMessage(),"Fatal Database Error",LOGFILE,timePrint());
}

// Add elements
try
{
    echo 'We get to here';
    var_dump($numberOfElements);
    for ($j = 1; $j < $numberOfElements; $j++)
    {
        echo 'We got this far';

        $stmt = $pdo->prepare("INSERT INTO tbl_elements (TaskDesc, TaskComments, TaskID) VALUES (:description, :comment, :taskid)");
        $stmt->bindParam(':description',    $_POST['taskDesc_'.$j]);
        $stmt->bindParam(':comment',        $_POST['taskComment_'.$j]);
        $stmt->bindParam(':taskid',         $taskID);
        $stmt->execute();
        var_dump($_SESSION['taskDesc_1']);
        var_dump($_SESSION['taskDesc_2']);
        var_dump($_SESSION['taskDesc_3']);
        var_dump($_SESSION['taskDesc_4']);
        var_dump($_SESSION['taskDesc_5']);

    }
}
catch (PDOException $ex)
{
    errorHandler($ex->getMessage(),"Fatal Database Error",LOGFILE,timePrint());
}


function generateNewID($db)
{
    $stmt = $db->prepare("SELECT * FROM tbl_tasks ORDER BY TaskID DESC LIMIT 1");
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    $taskID = $result['TaskID'];

    // Increment it
    $taskID++;
    return $taskID;
}
