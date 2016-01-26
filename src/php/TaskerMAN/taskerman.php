<?php
/**
 * TODO Insert PHPDoc comment here
 * TODO PHPUnit tests for various sequences
 * @author OLiver Earl
 */
require('init.php');
// If the user isn't logged in, redirect them back to the login page
if (!isset($_SESSION['login_auth']))
{
    header('Location: index.php');
}
?>
    <!DOCTYPE HTML>
    <html lang="en">
    <head>
        <?php include('meta.php'); ?>
        <title><?php echo APP_NAME . ' ' . APP_VER; ?></title>
    </head>
    <body>
    <!-- Container -->
    <div class="container">
        <!-- Header -->
        <?php include('header.php'); ?>
        <!-- Main -->
        <table id="name">
            <thead>
            <tr>
                <th>Task ID</th>
                <th>Task Name</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Status</th>
                <th>Task Owner</th>
            </tr>
            </thead>
            <tbody>
            <?php
            try
            {
                // Retrieve all tasks
                $stmt = $pdo->prepare("SELECT * FROM tbl_tasks ORDER BY TaskID");
                $stmt->execute();

                // Print
                while($row = $stmt->fetch(PDO::FETCH_ASSOC))
                {
                    echo '<tr>';
                    echo '<td>'.$row['TaskID'].'</td>';
                    echo '<td>'.$row['TaskName'].'</td>';
                    echo '<td>'.$row['StartDate'].'</td>';
                    echo '<td>'.$row['EndDate'].'</td>';
                    echo '<td>'.convertStatus($row['Status']).'</td>';
                    echo '<td>'.retrieveNames($row['TaskOwner'],$pdo).'</td>';
                    echo '</tr>';
                }
            }
            catch (PDOException $ex)
            {
                errorHandler($ex->getMessage(),"Fatal Database Error",LOGFILE,timePrint());
            }
            ?>
            </tbody>
        </table>
        <!-- Footer -->
        <?php include('footer.php'); ?>
    </div>

    <!-- Modal Windows -->
    <!-- View Task -->
    <?php include('taskView.php'); ?>

    <!-- Add Task -->
    <?php include('taskAdd.php'); ?>

    <!-- Delete Task -->
    <?php include('taskDelete.php'); ?>
    </body>
    </html>
<?php

function convertStatus($retrievedInteger)
{
    switch($retrievedInteger)
    {
        case 0:
            return 'Abandoned';
            break;
        case 1:
            return 'Allocated';
            break;
        case 2:
            return 'Completed';
            break;
        default:
            return 'Unknown';
    }
}

function retrieveNames($email, $database)
{
    try
    {
        $stmt = $database->prepare("SELECT FirstName, LastName from tbl_users WHERE Email = :email");
        $stmt->bindParam(':email', $email);
        $stmt->execute();

        // Retrieve output and check
        $output = $stmt->fetch(PDO::FETCH_ASSOC);

        if (isset($output['FirstName']) & isset($output['LastName'])) // Smelly
        {
            $fullName = $output['FirstName'] . ' ' . $output['LastName'];
            return $fullName;
        }
        else
        {
            return $email;
        }
    } catch (PDOException $ex)
    {
        errorHandler($ex->getMessage(),"Fatal Database Error",LOGFILE,timePrint());
        return $email;
    }
}
