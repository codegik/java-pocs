import chromadb
import os

os.environ['CURL_CA_BUNDLE'] = '<put local cert here>'
chromadb.is_thin_client = False

from chromadb.config import Settings
from chromadb.utils import embedding_functions

embedder = embedding_functions.DefaultEmbeddingFunction()
embedder_sentence = embedding_functions.SentenceTransformerEmbeddingFunction(model_name="all-MiniLM-L6-v2")

client = chromadb.Client(Settings(chroma_db_impl="duckdb+parquet", persist_directory="db/"))

student_info = """
Alexandra Thompson, a 19-year-old computer science sophomore with a 3.7 GPA,
is a member of the programming and chess clubs who enjoys pizza, swimming, and hiking
in her free time in hopes of working at a tech company after graduating from the University of Washington.
"""

club_info = """
The university chess club provides an outlet for students to come together and enjoy playing
the classic strategy game of chess. Members of all skill levels are welcome, from beginners learning
the rules to experienced tournament players. The club typically meets a few times per week to play casual games,
participate in tournaments, analyze famous chess matches, and improve members' skills.
"""

university_info = """
The University of Washington, founded in 1861 in Seattle, is a public research university
with over 45,000 students across three campuses in Seattle, Tacoma, and Bothell.
As the flagship institution of the six public universities in Washington state,
UW encompasses over 500 buildings and 20 million square feet of space,
including one of the largest library systems in the world.
"""

collection = client.get_or_create_collection(name="Students")

collection.add(
    embeddings=[embedder_sentence],
    documents=[student_info, club_info, university_info],
    metadatas=[{"source": "student info"}, {"source": "club info"}, {'source': 'university info'}],
    ids=["id1", "id2", "id3"]
)

results = collection.query(
    query_texts=["Give me students id1, id2 and id3"],
    n_results=3
)

print(results)

results = collection.query(
    query_texts=["What is the student name?"],
    n_results=2
)

print(results)

collection.delete(ids=["id1", "id2", "id3"])
