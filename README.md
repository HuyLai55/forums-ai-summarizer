
## Forums GPT Summarizer
___

<h3> About</h3>
---
this project is a comments summarizer that leverages
GPT AI (GPT-3.5 model) to generate summaries of the 
thread's comment. Comments of per thread is crawled by 
jsoup framework. Then comments is saved to database by 
sqlite tool. It starts by making a request to a specified 
forum thread, crawling all information in thread and per 
comment by jsoup framework, using sqlite to saved them to 
database. 

These comments are concatenated into groups of a specified
number of tokens, a summary is generated for each group by
prompting the OpenAi API with the group's text and the title
of the forum thread. The summaries are then saved to database
with the id of thread is saved.


### Usage
___
* Run the code.
* Enter the link URL and source of thread.
* The summarized of comments in thread will be displayed.





