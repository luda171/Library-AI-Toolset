import pandas as pd
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import joblib
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score,precision_recall_fscore_support
#adjust to your project dir
progect_dir="{project}"
def calculate_metrics_f(df,fi1,fi2):
  # Calculate the accuracy.
  accuracy = accuracy_score(y_true=df[fi1], y_pred=df["predicted_label"])
  # Calculate the precision.
  wprecision = precision_score(y_true=df[fi1], y_pred=df["predicted_label"], average="weighted")
  # Calculate the recall.
  wrecall = recall_score(y_true=df[fi1], y_pred=df["predicted_label"], average="weighted")
  # Calculate the F1 score.
  wf1 = f1_score(y_true=df[fi1], y_pred=df["predicted_label"], average="weighted")
 # Calculate precision, recall, and F1-score per class
  precision, recall, f1, _ = precision_recall_fscore_support(y_true=df[fi1], y_pred=df["predicted_label"], average=None)

  return {
    "accuracy": accuracy,
    "wprecision": wprecision,
    "wrecall": wrecall,
    "wf1": wf1,
    "precision": precision.tolist(),
    "recall": recall.tolist(),
    "f1": f1.tolist()
  }

def calculate_metrics(df):
  # Calculate the accuracy.
  accuracy = accuracy_score(y_true=df["regex_label"], y_pred=df["predicted_label"])
  # Calculate the precision.
  wprecision = precision_score(y_true=df["regex_label"], y_pred=df["predicted_label"], average="weighted")
  # Calculate the recall.
  wrecall = recall_score(y_true=df["regex_label"], y_pred=df["predicted_label"], average="weighted")
  # Calculate the F1 score.
  wf1 = f1_score(y_true=df["regex_label"], y_pred=df["predicted_label"], average="weighted")
 # Calculate precision, recall, and F1-score per class
  precision, recall, f1, _ = precision_recall_fscore_support(y_true=df["regex_label"], y_pred=df["predicted_label"], average=None)

  return {
    "accuracy": accuracy,
    "wprecision": wprecision,
    "wrecall": wrecall,
    "wf1": wf1,
    "precision": precision.tolist(),
    "recall": recall.tolist(),
    "f1": f1.tolist()
  }

# Read your test data from a CSV file

def label_predicted(test_df,results_path):
    
    # Load your saved machine learning model
    
  
    #model based on sent
    model = AutoModelForSequenceClassification.from_pretrained(project_dir+"/models/scibert_finetuned_4_5000_2_intent_zukerberg_sent")
  
    # Load the tokenizer
    tokenizer = AutoTokenizer.from_pretrained("allenai/scibert_scivocab_uncased")
    
    # Function to predict the label for a given text
    def predict_label(text):
        inputs = tokenizer(text, truncation=True, padding=True, max_length=512, return_tensors="pt")
        with torch.no_grad():
            outputs = model(**inputs)
        logits = outputs.logits
        predicted_label = torch.argmax(logits, dim=1).item()
        return predicted_label
    
    # Create a new DataFrame to store the results
    #results_df = filtered_df.copy()
    print(test_df.head())
    # Iterate through each row in the test data and predict labels
    for index, row in test_df.iterrows():
        text = row['text']
        predicted_label = predict_label(text)
        #print(predicted_label,text)   
        test_df.at[index, 'predicted_intent_bert_s'] = predicted_label
    
    # Write the results DataFrame to a new CSV file
    test_df.to_csv(results_path, index=False)
    

#f_df = df[(df['predicted_label'] == 0.0) | (df['predicted_label'] == 1.0)]
#adjust to your data file 
df = pd.read_csv(project_dir+"/data/cit_url_data_results_2_corr_intentbert_01.csv")
output_file = project_dir+"/output//cit_url_data_results_2_corr_intentbert_01_sent_llama.csv"

label_predicted(df,project_dir+"/data//cit_url_data_results_2_corr_intentbert_01_sent.csv")
# Calculate the accuracy, precision, recall, and F1 score.
#metrics = calculate_metrics(df)

# Print the accuracy, precision, recall, and F1 score.
#print("Accuracy:", metrics["accuracy"])
#print("WPrecision:", metrics["wprecision"])
#print("WRecall:", metrics["wrecall"])
#print("WF1 score:", metrics["wf1"])
#print("Precision:", metrics["precision"])
#print("Recall:", metrics["recall"])
#print("F1 score:", metrics["f1"])


