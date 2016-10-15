# Aberystwyth University CS221 2015/2016 - Group 05 #

For this module we had to create a system capable of creating tasks, assigning them to users and view them locally even offline.The three components which made up the program were:

- TaskerCLI - A Java application which ran locally and allowed a user to view their tasks
- TaskerMAN - A website which connected to a SQL server and provided a user with the ability to manage tasks such as creating/editing/deleting them.
- TaskerSRV - BASH scripts which create a SQL server with the correct tables, and FK constraints. 

This is a fork of the original project which is now archived. In this project we aim to fix the shortfalls we identified in the original. This includes steps such as:

- Create unit tests and a harness which exercises code paths and run to test for regressions
- Move to use a sustainable git workflow - namely using pull requests instead of commiting directly to master
- Use MVC to power the GUIs
- Correctly use threading for the database and remove several race conditions
- Document the program as new features are added instead of down the line
- Build documentation.
