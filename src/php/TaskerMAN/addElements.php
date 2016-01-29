<div id="addElements" class="modalWindow">
    <div>
        <div class="modal">
            <script src="js/entryValidation.js"></script>
            <form action="addEntry.php" method="POST">
                <fieldset class="info_box">
                    <a href="taskerman.php" title="Close" class="close">X</a>
                    <?php
                    // var_dump($_SESSION['add_numberOfElements']);
                    // echo '<input type="text" name="taskDesc_1" required />';
                    for ($i = 1; $i <= ($_SESSION['add_numberOfElements']); $i++)
                    {

                        // Print a box for task description and comment
                        echo '<label for="taskDesc_' . $i . '">Task Description ' . $i .
                            '</label><br/>';
                        echo '<input name="taskDesc_' . $i . '" type="text" id="taskDesc_' . $i . '" required /><br/>';

                        echo '<label for="taskComment_' . $i . '">Task Comment ' . $i .
                            '</label><br/>';
                        echo '<input name="taskComment_' . $i . '" type="text" id="taskComment_' . $i . '" required /><br/>';
                    } ?>
                    <input name="submit" class="modalButton" type="submit" value="Submit" />
                    <input name="clear" class="modalButton" type="reset" value="Clear" />
                </fieldset>
            </form>
        </div>
    </div>
</div>